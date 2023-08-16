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
package io.rsocket.keepalive;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.rsocket.frame.SocketKeepAliveFrameCodec;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public abstract class SocketKeepAliveSupport implements SocketKeepAliveFrameAcceptor {
  final ByteBufAllocator allocator;
  final Scheduler scheduler;
  final Duration keepAliveInterval;
  final Duration keepAliveTimeout;
  final long keepAliveTimeoutMillis;

  volatile int state;
  static final AtomicIntegerFieldUpdater<SocketKeepAliveSupport> STATE =
      AtomicIntegerFieldUpdater.newUpdater(SocketKeepAliveSupport.class, "state");

  static final int STOPPED_STATE = 0;
  static final int STARTING_STATE = 1;
  static final int STARTED_STATE = 2;
  static final int DISPOSED_STATE = -1;

  volatile Consumer<KeepAlive> onTimeout;
  volatile Consumer<ByteBuf> onFrameSent;

  Disposable ticksDisposable;

  volatile long lastReceivedMillis;

  public SocketKeepAliveSupport(
      ByteBufAllocator allocator, int keepAliveInterval, int keepAliveTimeout) {
    this.allocator = allocator;
    this.scheduler = Schedulers.parallel();
    this.keepAliveInterval = Duration.ofMillis(keepAliveInterval);
    this.keepAliveTimeout = Duration.ofMillis(keepAliveTimeout);
    this.keepAliveTimeoutMillis = keepAliveTimeout;
  }

  public SocketKeepAliveSupport start() {
    if (this.state == STOPPED_STATE && STATE.compareAndSet(this, STOPPED_STATE, STARTING_STATE)) {
      this.lastReceivedMillis = scheduler.now(TimeUnit.MICROSECONDS);
    }

    this.ticksDisposable =
        Flux.interval(keepAliveInterval, scheduler).subscribe(v -> onIntervalTick());

    if (this.state != STARTING_STATE || !STATE.compareAndSet(this, STARTING_STATE, STARTED_STATE)) {
      ticksDisposable.dispose();
    }

    return this;
  }

  abstract void onIntervalTick();

  public void send(ByteBuf frame) {
    if (onFrameSent != null) {
      onFrameSent.accept(frame);
    }
  }

  public void stop() {
    terminate(STOPPED_STATE);
  }

  public SocketKeepAliveSupport onSendKeepAliveFrame(Consumer<ByteBuf> onFrameSent) {
    this.onFrameSent = onFrameSent;
    return this;
  }

  public SocketKeepAliveSupport onTimeout(Consumer<SocketKeepAliveSupport.KeepAlive> onTimeout) {
    this.onTimeout = onTimeout;
    return this;
  }

  @Override
  public void receive(ByteBuf keepAliveFrame) {
    this.lastReceivedMillis = scheduler.now(TimeUnit.MILLISECONDS);
    send(
        SocketKeepAliveFrameCodec.encode(
            allocator, SocketKeepAliveFrameCodec.data(keepAliveFrame).retain()));
  }

  @Override
  public void dispose() {
    terminate(DISPOSED_STATE);
  }

  @Override
  public boolean isDisposed() {
    return ticksDisposable.isDisposed();
  }

  void tryTimeout() {
    long now = scheduler.now(TimeUnit.MILLISECONDS);
    if (now - lastReceivedMillis >= keepAliveTimeoutMillis) {
      if (onTimeout != null) {
        onTimeout.accept(new KeepAlive(keepAliveInterval, keepAliveTimeout));
      }
      stop();
    }
  }

  void terminate(int terminationState) {
    for (; ; ) {
      final int state = this.state;
      if (state == STOPPED_STATE || state == DISPOSED_STATE) {
        return;
      }

      final Disposable disposable = this.ticksDisposable;
      if (STATE.compareAndSet(this, state, terminationState)) {
        disposable.dispose();
        return;
      }
    }
  }

  @Getter
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  @RequiredArgsConstructor
  public static class KeepAlive {
    Duration tickPeriod;
    Duration timeoutMillis;
  }
}
