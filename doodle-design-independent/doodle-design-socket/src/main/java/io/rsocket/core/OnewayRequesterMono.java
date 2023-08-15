/*
 * Copyright (c) 2022-present Doodle. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rsocket.core;

import static io.rsocket.core.PayloadValidationUtils.INVALID_PAYLOAD_ERROR_MESSAGE;
import static io.rsocket.core.PayloadValidationUtils.isValid;
import static io.rsocket.core.StateUtils.*;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.IllegalReferenceCountException;
import io.rsocket.Payload;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import org.doodle.design.socket.SocketConnection;
import org.doodle.design.socket.SocketRequesterResponderSupport;
import org.doodle.design.socket.frame.SocketFrameType;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Exceptions;
import reactor.core.Scannable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.util.annotation.Nullable;

public final class OnewayRequesterMono extends Mono<Void> implements Subscription, Scannable {

  volatile long state;

  static final AtomicLongFieldUpdater<OnewayRequesterMono> STATE =
      AtomicLongFieldUpdater.newUpdater(OnewayRequesterMono.class, "state");

  final Payload payload;

  final ByteBufAllocator allocator;
  final int mtu;
  final int maxFrameLength;
  final SocketRequesterResponderSupport requesterResponderSupport;
  final SocketConnection connection;

  public OnewayRequesterMono(
      Payload payload, SocketRequesterResponderSupport requesterResponderSupport) {
    this.allocator = requesterResponderSupport.getAllocator();
    this.payload = payload;
    this.mtu = requesterResponderSupport.getMtu();
    this.maxFrameLength = requesterResponderSupport.getMaxFrameLength();
    this.requesterResponderSupport = requesterResponderSupport;
    this.connection = requesterResponderSupport.getSocketConnection();
  }

  @Override
  public void subscribe(CoreSubscriber<? super Void> actual) {
    long previousState = markSubscribed(STATE, this);
    if (isSubscribedOrTerminated(previousState)) {
      final IllegalStateException e = new IllegalStateException("OnewayRequesterMono 只允许单订阅");
      Operators.error(actual, e);
      return;
    }

    actual.onSubscribe(this);

    final Payload p = this.payload;
    int mtu = this.mtu;
    try {
      if (!isValid(mtu, this.maxFrameLength, p, false)) {
        lazyTerminate(STATE, this);
        final IllegalArgumentException e =
            new IllegalArgumentException(
                String.format(INVALID_PAYLOAD_ERROR_MESSAGE, this.maxFrameLength));
        p.release();
        actual.onError(e);
        return;
      }
    } catch (IllegalReferenceCountException e) {
      lazyTerminate(STATE, this);
      actual.onError(e);
      return;
    }

    try {
      if (isTerminated(this.state)) {
        p.release();
        return;
      }

      sendReleasingPayload(SocketFrameType.ONEWAY, mtu, p, this.connection, this.allocator, true);
    } catch (Throwable e) {
      lazyTerminate(STATE, this);
      actual.onError(e);
      return;
    }

    lazyTerminate(STATE, this);
    actual.onComplete();
  }

  @Override
  public void request(long n) {
    // no ops
  }

  @Override
  public void cancel() {
    markTerminated(STATE, this);
  }

  @Override
  @Nullable
  public Void block(Duration m) {
    return block();
  }

  @Override
  @Nullable
  public Void block() {
    long previousState = markSubscribed(STATE, this);
    if (isSubscribedOrTerminated(previousState)) {
      throw new IllegalStateException("OnewayRequesterMono 只允许单订阅");
    }

    final Payload p = this.payload;
    try {
      if (!isValid(this.mtu, this.maxFrameLength, p, false)) {
        lazyTerminate(STATE, this);
        final IllegalArgumentException e =
            new IllegalArgumentException(
                String.format(INVALID_PAYLOAD_ERROR_MESSAGE, this.maxFrameLength));
        p.release();
        throw e;
      }
    } catch (IllegalReferenceCountException e) {
      lazyTerminate(STATE, this);
      throw Exceptions.propagate(e);
    }

    try {
      sendReleasingPayload(
          SocketFrameType.ONEWAY, this.mtu, this.payload, this.connection, this.allocator, true);
    } catch (Throwable e) {
      lazyTerminate(STATE, this);
      throw Exceptions.propagate(e);
    }

    lazyTerminate(STATE, this);
    return null;
  }

  @Override
  public Object scanUnsafe(Scannable.Attr key) {
    return null;
  }

  static void sendReleasingPayload(
      SocketFrameType frameType,
      int mtu,
      Payload payload,
      SocketConnection connection,
      ByteBufAllocator allocator,
      boolean requester) {}
}
