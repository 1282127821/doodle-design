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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.rsocket.Closeable;
import io.rsocket.RSocketErrorException;
import io.rsocket.SocketConnection;
import io.rsocket.frame.SocketFrameHeaderCodec;
import io.rsocket.frame.SocketFrameType;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import lombok.ToString;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.netty.Connection;

public class SocketClientServerInputMultiplexer implements CoreSubscriber<ByteBuf>, Closeable {

  private final InternalSocketConnection serverReceiver;
  private final InternalSocketConnection clientReceiver;
  private final SocketConnection source;
  private final boolean isClient;

  private Subscription s;

  private Throwable t;

  private volatile int state;
  private static final AtomicIntegerFieldUpdater<SocketClientServerInputMultiplexer> STATE =
      AtomicIntegerFieldUpdater.newUpdater(SocketClientServerInputMultiplexer.class, "state");

  public SocketClientServerInputMultiplexer(SocketConnection source, boolean isClient) {
    this.source = source;
    this.isClient = isClient;

    this.serverReceiver = new InternalSocketConnection(Type.SERVER, this, source);
    this.clientReceiver = new InternalSocketConnection(Type.CLIENT, this, source);
  }

  public SocketConnection asServerConnection() {
    return serverReceiver;
  }

  public SocketConnection asClientConnection() {
    return clientReceiver;
  }

  @Override
  public Mono<Void> onClose() {
    return source.onClose();
  }

  @Override
  public void onSubscribe(Subscription s) {
    if (Operators.validate(this.s, s)) {
      this.s = s;
      s.request(Long.MAX_VALUE);
    }
  }

  @Override
  public void onNext(ByteBuf frame) {
    final Type type;
    if (SocketFrameHeaderCodec.frameType(frame) == SocketFrameType.KEEP_ALIVE) {
      type = isClient ? Type.CLIENT : Type.SERVER;
    } else {
      type = isClient ? Type.SERVER : Type.CLIENT;
    }
    switch (type) {
      case CLIENT -> clientReceiver.onNext(frame);
      case SERVER -> serverReceiver.onNext(frame);
    }
  }

  @Override
  public void onError(Throwable t) {
    this.t = t;

    final int previousState = STATE.getAndSet(this, Integer.MIN_VALUE);
    if (previousState == Integer.MIN_VALUE || previousState == 0) {
      return;
    }

    if (clientReceiver.isSubscribed()) {
      clientReceiver.onError(t);
    }
    if (serverReceiver.isSubscribed()) {
      serverReceiver.onError(t);
    }
  }

  @Override
  public void onComplete() {
    final int previousState = STATE.getAndSet(this, Integer.MIN_VALUE);
    if (previousState == Integer.MIN_VALUE || previousState == 0) {
      return;
    }

    if (clientReceiver.isSubscribed()) {
      clientReceiver.onComplete();
    }
    if (serverReceiver.isSubscribed()) {
      serverReceiver.onComplete();
    }
  }

  @Override
  public void dispose() {
    source.dispose();
  }

  @Override
  public boolean isDisposed() {
    return source.isDisposed();
  }

  boolean notifyRequested() {
    final int currentState = incrementAndGetCheckingState();
    if (currentState == Integer.MIN_VALUE) {
      return false;
    }

    if (currentState == 2) {
      source.receive().subscribe(this);
    }

    return true;
  }

  int incrementAndGetCheckingState() {
    int prev, next;
    for (; ; ) {
      prev = this.state;

      if (prev == Integer.MIN_VALUE) {
        return prev;
      }

      next = prev + 1;
      if (STATE.compareAndSet(this, prev, next)) {
        return next;
      }
    }
  }

  public enum Type {
    SERVER,
    CLIENT
  }

  @ToString
  private static class InternalSocketConnection extends Flux<ByteBuf>
      implements Subscription, SocketConnection {
    private final Type type;
    private final SocketClientServerInputMultiplexer clientServerInputMultiplexer;
    private final SocketConnection source;

    private volatile int state;
    static final AtomicIntegerFieldUpdater<InternalSocketConnection> STATE =
        AtomicIntegerFieldUpdater.newUpdater(InternalSocketConnection.class, "state");

    CoreSubscriber<? super ByteBuf> actual;

    public InternalSocketConnection(
        Type type,
        SocketClientServerInputMultiplexer clientServerInputMultiplexer,
        SocketConnection source) {
      this.type = type;
      this.clientServerInputMultiplexer = clientServerInputMultiplexer;
      this.source = source;
    }

    @Override
    public void subscribe(CoreSubscriber<? super ByteBuf> actual) {
      if (this.state == 0 && STATE.compareAndSet(this, 0, 1)) {
        this.actual = actual;
        actual.onSubscribe(this);
      } else {
        Operators.error(
            actual,
            new IllegalStateException("InternalSocketConnection allows only single subscription"));
      }
    }

    @Override
    public void request(long n) {
      if (this.state == 1 && STATE.compareAndSet(this, 1, 2)) {
        final SocketClientServerInputMultiplexer multiplexer = clientServerInputMultiplexer;
        if (!multiplexer.notifyRequested()) {
          final Throwable t = multiplexer.t;
          if (t != null) {
            this.actual.onError(t);
          } else {
            this.actual.onComplete();
          }
        }
      }
    }

    @Override
    public void cancel() {
      // no ops
    }

    void onNext(ByteBuf frame) {
      this.actual.onNext(frame);
    }

    void onComplete() {
      this.actual.onComplete();
    }

    void onError(Throwable t) {
      this.actual.onError(t);
    }

    @Override
    public void sendFrame(int streamId, ByteBuf frame) {
      source.sendFrame(streamId, frame);
    }

    @Override
    public void sendErrorAndClose(RSocketErrorException e) {
      source.sendErrorAndClose(e);
    }

    @Override
    public Flux<ByteBuf> receive() {
      return this;
    }

    @Override
    public ByteBufAllocator alloc() {
      return source.alloc();
    }

    @Override
    public SocketAddress remoteAddress() {
      return source.remoteAddress();
    }

    @Override
    public void dispose() {
      source.dispose();
    }

    @Override
    public boolean isDisposed() {
      return source.isDisposed();
    }

    public boolean isSubscribed() {
      return this.state != 0;
    }

    @Override
    public Mono<Void> onClose() {
      return source.onClose();
    }

    @Override
    public double availability() {
      return source.availability();
    }

    @Override
    public Connection connection() {
      return source.connection();
    }
  }
}
