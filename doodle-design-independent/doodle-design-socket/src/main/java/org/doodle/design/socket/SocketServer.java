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
import io.rsocket.Closeable;
import io.rsocket.frame.decoder.PayloadDecoder;
import java.time.Duration;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.doodle.design.socket.frame.SocketFrameHeaderCodec;
import org.doodle.design.socket.frame.SocketFrameType;
import org.doodle.design.socket.transport.SocketServerTransport;
import reactor.core.publisher.Mono;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class SocketServer {
  SocketAcceptor serverAcceptor;
  private int mtu = 0;
  PayloadDecoder payloadDecoder;
  Duration timeout;

  private Mono<Void> acceptor(
      SocketServerSetup serverSetup, SocketConnection socketConnection, int maxFrameLength) {
    return serverSetup
        .init(socketConnection)
        .flatMap(
            tuple2 -> {
              final ByteBuf startFrame = tuple2.getT1();
              final SocketConnection connection = (SocketConnection) tuple2.getT2();
              return accept(serverSetup, startFrame, connection, maxFrameLength);
            });
  }

  private Mono<Void> accept(
      SocketServerSetup serverSetup,
      ByteBuf startFrame,
      SocketConnection connection,
      int maxFrameLength) {
    SocketFrameType frameType = SocketFrameHeaderCodec.frameType(startFrame);
    if (frameType != SocketFrameType.SETUP) {
      log.info("没有按照规定先发送 setup 协议!");
      return connection.onClose();
    }

    return serverSetup.acceptSetup(
        startFrame,
        connection,
        (keepAliveHandler, socketConnection) -> {
          SocketConnectionSetupPayload setupPayload =
              new SocketConnectionSetupPayload(startFrame.retain());

          SocketRequester requester =
              new SocketRequester(
                  socketConnection,
                  payloadDecoder,
                  mtu,
                  maxFrameLength,
                  setupPayload.keepAliveInterval(),
                  setupPayload.keepAliveMaxLifetime(),
                  keepAliveHandler);

          return serverAcceptor
              .apply(setupPayload, requester)
              .doOnNext(
                  socketHandler -> {
                    SocketResponder responder =
                        new SocketResponder(
                            mtu, maxFrameLength, socketConnection, socketHandler, payloadDecoder);
                  })
              .doFinally(signalType -> setupPayload.release())
              .then();
        });
  }

  public <T extends Closeable> Mono<T> bind(SocketServerTransport<T> transport) {
    return Mono.defer(
        new Supplier<Mono<T>>() {
          final SocketServerSetup serverSetup = new SocketServerSetup(timeout);

          @Override
          public Mono<T> get() {
            int maxFrameLength = transport.maxFrameLength();
            return transport
                .start(socketConnection -> acceptor(serverSetup, socketConnection, maxFrameLength))
                .doOnNext(c -> c.onClose().doFinally(v -> serverSetup.dispose()).subscribe());
          }
        });
  }
}
