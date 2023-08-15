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
package org.doodle.design.socket;

import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;
import io.rsocket.core.OnewayRequesterMono;
import io.rsocket.frame.decoder.PayloadDecoder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.doodle.design.socket.keepalive.SocketClientKeepAliveSupport;
import org.doodle.design.socket.keepalive.SocketKeepAliveFrameAcceptor;
import org.doodle.design.socket.keepalive.SocketKeepAliveHandler;
import org.doodle.design.socket.keepalive.SocketKeepAliveSupport;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketRequester extends SocketRequesterResponderSupport implements Socket {
  SocketKeepAliveFrameAcceptor keepAliveFrameAcceptor;
  Sinks.Empty<Void> onClose;

  public SocketRequester(
      SocketConnection socketConnection,
      PayloadDecoder payloadDecoder,
      int mtu,
      int maxFrameLength,
      int keepAliveTickPeriod,
      int keepAliveAckTimeout,
      SocketKeepAliveHandler keepAliveHandler) {
    super(mtu, maxFrameLength, socketConnection, payloadDecoder);

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

  private void tryTerminateOnKeepAlive(SocketKeepAliveSupport.KeepAlive keepAlive) {}

  private void handleIncomingFrames(ByteBuf byteBuf) {}

  private void tryShutdown() {}

  private void tryTerminateOnConnectionError(Throwable throwable) {}

  @Override
  public Mono<Void> oneway(Payload payload) {
    return new OnewayRequesterMono(payload, this);
  }

  @Override
  public Mono<Void> onClose() {
    return onClose.asMono();
  }

  @Override
  public boolean isDisposed() {
    return false;
  }

  @Override
  public void dispose() {
    tryShutdown();
  }
}
