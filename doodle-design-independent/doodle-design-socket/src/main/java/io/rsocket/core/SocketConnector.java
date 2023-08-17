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
import io.rsocket.*;
import io.rsocket.frame.SocketSetupFrameCodec;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.keepalive.SocketKeepAliveHandler;
import io.rsocket.transport.SocketClientTransport;
import io.rsocket.util.EmptyPayload;
import java.time.Duration;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SocketConnector {
  SocketAcceptor acceptor;
  Mono<Payload> setupPayloadMono;
  PayloadDecoder payloadDecoder;
  Duration keepAliveInterval;
  Duration keepAliveMaxLifeTime;
  int mtu;

  public Mono<Socket> connect(Supplier<SocketClientTransport> transportSupplier) {
    return Mono.fromSupplier(transportSupplier)
        .flatMap(
            ct -> {
              int maxFrameLength = ct.maxFrameLength();
              return ct.connect()
                  .flatMap(
                      connection ->
                          setupPayloadMono
                              .defaultIfEmpty(EmptyPayload.INSTANCE)
                              .map(setupPayload -> Tuples.of(connection, setupPayload))
                              .doOnError(ex -> connection.dispose())
                              .doOnCancel(connection::dispose))
                  .flatMap(
                      tuple2 -> {
                        SocketConnection socketConnection = tuple2.getT1();
                        Payload setupPayload = tuple2.getT2();
                        SocketClientSetup clientSetup = new SocketClientSetup();

                        ByteBuf setupFrame =
                            SocketSetupFrameCodec.encode(
                                socketConnection.alloc(),
                                setupPayload,
                                (int) keepAliveInterval.toMillis(),
                                (int) keepAliveMaxLifeTime.toMillis());
                        socketConnection.sendFrame(0, setupFrame.retainedSlice());

                        return clientSetup
                            .init(socketConnection)
                            .flatMap(
                                tuple -> {
                                  ByteBuf serverResponse = tuple.getT1();
                                  SocketConnection connection = tuple.getT2();
                                  SocketClientServerInputMultiplexer multiplexer =
                                      new SocketClientServerInputMultiplexer(connection, true);

                                  SocketRequester requester =
                                      new SocketRequester(
                                          multiplexer.asClientConnection(),
                                          payloadDecoder,
                                          mtu,
                                          maxFrameLength,
                                          (int) keepAliveInterval.toMillis(),
                                          (int) keepAliveMaxLifeTime.toMillis(),
                                          new SocketKeepAliveHandler(connection));
                                  SocketConnectionSetupPayload setup =
                                      new SocketConnectionSetupPayload(setupFrame);

                                  return acceptor
                                      .apply(setup, requester)
                                      .map(
                                          socketHandler -> {
                                            SocketResponder responder =
                                                new SocketResponder(
                                                    mtu,
                                                    maxFrameLength,
                                                    multiplexer.asServerConnection(),
                                                    socketHandler,
                                                    payloadDecoder);
                                            return socketHandler;
                                          })
                                      .doFinally(signalType -> setup.release());
                                });
                      });
            });
  }
}
