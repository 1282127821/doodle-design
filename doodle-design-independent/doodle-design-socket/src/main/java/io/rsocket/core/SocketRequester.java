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
import io.rsocket.Payload;
import io.rsocket.Socket;
import io.rsocket.SocketConnection;
import io.rsocket.exceptions.ConnectionErrorException;
import io.rsocket.frame.SocketFrameHeaderCodec;
import io.rsocket.frame.SocketFrameType;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.keepalive.SocketClientKeepAliveSupport;
import io.rsocket.keepalive.SocketKeepAliveFrameAcceptor;
import io.rsocket.keepalive.SocketKeepAliveHandler;
import io.rsocket.keepalive.SocketKeepAliveSupport;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class SocketRequester extends SocketRequesterResponderSupport implements Socket {
  private static final Exception CLOSED_CHANNEL_EXCEPTION = new ClosedChannelException();

  static {
    CLOSED_CHANNEL_EXCEPTION.setStackTrace(new StackTraceElement[0]);
  }

  private volatile Throwable terminationError;
  private static final AtomicReferenceFieldUpdater<SocketRequester, Throwable> TERMINATION_ERROR =
      AtomicReferenceFieldUpdater.newUpdater(
          SocketRequester.class, Throwable.class, "terminationError");
  final SocketKeepAliveFrameAcceptor keepAliveFrameAcceptor;
  final Sinks.Empty<Void> onClose;

  public SocketRequester(
      SocketConnection socketConnection,
      PayloadDecoder payloadDecoder,
      int maxFrameLength,
      int keepAliveTickPeriod,
      int keepAliveAckTimeout,
      SocketKeepAliveHandler keepAliveHandler) {
    super(maxFrameLength, socketConnection, payloadDecoder);

    onClose = Sinks.empty();

    socketConnection
        .onClose()
        .subscribe(null, this::tryTerminateOnConnectionError, this::tryShutdown);

    socketConnection.receive().subscribe(this::handleIncomingFrames, e -> {});

    if (keepAliveTickPeriod != 0 && keepAliveHandler != null) {
      SocketKeepAliveSupport keepAliveSupport =
          new SocketClientKeepAliveSupport(
              getAllocator(), keepAliveTickPeriod, keepAliveAckTimeout);
      this.keepAliveFrameAcceptor =
          keepAliveHandler.start(
              keepAliveSupport,
              keepAliveFrame -> socketConnection.sendFrame(0, keepAliveFrame),
              this::tryTerminateOnKeepAlive);
    } else {
      this.keepAliveFrameAcceptor = null;
    }
  }

  private void handleIncomingFrames(ByteBuf frame) {
    SocketFrameType frameType = SocketFrameHeaderCodec.frameType(frame);
    if (frameType == SocketFrameType.KEEP_ALIVE) {
      if (keepAliveFrameAcceptor != null) {
        keepAliveFrameAcceptor.receive(frame);
      }
    } else {
      log.error("暂不支持的协议类型: {}", frameType);
    }
  }

  @Override
  public Mono<Void> oneway(Payload payload) {
    return new SocketOnewayRequesterMono(payload, this);
  }

  @Override
  public void dispose() {
    tryShutdown();
  }

  @Override
  public boolean isDisposed() {
    return terminationError != null;
  }

  @Override
  public Mono<Void> onClose() {
    return onClose.asMono();
  }

  private void tryShutdown() {
    if (terminationError == null) {
      if (TERMINATION_ERROR.compareAndSet(this, null, CLOSED_CHANNEL_EXCEPTION)) {
        terminate(CLOSED_CHANNEL_EXCEPTION);
      }
    }
  }

  private void tryTerminateOnKeepAlive(SocketKeepAliveSupport.KeepAlive keepAlive) {
    tryTerminate(() -> new ConnectionErrorException(""));
  }

  private void tryTerminateOnConnectionError(Throwable throwable) {
    tryTerminate(() -> throwable);
  }

  private void tryTerminate(Supplier<Throwable> errorSupplier) {
    if (terminationError == null) {
      Throwable e = errorSupplier.get();
      if (TERMINATION_ERROR.compareAndSet(this, null, e)) {
        terminate(e);
      }
    }
  }

  private void terminate(Throwable e) {
    if (keepAliveFrameAcceptor != null) {
      keepAliveFrameAcceptor.dispose();
    }
    getSocketConnection().dispose();

    if (e == CLOSED_CHANNEL_EXCEPTION) {
      onClose.tryEmitEmpty();
    } else {
      onClose.tryEmitError(e);
    }
  }
}
