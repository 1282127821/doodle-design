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
import io.rsocket.frame.decoder.SocketPayloadDecoder;
import io.rsocket.keepalive.SocketKeepAliveHandler;
import io.rsocket.transport.SocketClientTransport;
import io.rsocket.util.DefaultPayload;
import io.rsocket.util.EmptyPayload;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocketConnector {
  SocketAcceptor acceptor;
  Mono<Payload> setupPayloadMono = Mono.empty();
  SocketPayloadDecoder payloadDecoder = SocketPayloadDecoder.ZERO_COPY;
  Duration keepAliveInterval = Duration.ofSeconds(20);
  Duration keepAliveMaxLifeTime = Duration.ofSeconds(90);

  public static SocketConnector create() {
    return new SocketConnector();
  }

  public SocketConnector acceptor(SocketAcceptor acceptor) {
    Objects.requireNonNull(acceptor);
    this.acceptor = acceptor;
    return this;
  }

  public static Mono<Socket> connectWith(SocketClientTransport transport) {
    return create().connect(() -> transport);
  }

  public SocketConnector setupPayload(Mono<Payload> setupPayloadMono) {
    this.setupPayloadMono = setupPayloadMono;
    return this;
  }

  public SocketConnector setupPayload(Payload payload) {
    if (payload instanceof DefaultPayload) {
      this.setupPayloadMono = Mono.just(payload);
    } else {
      this.setupPayloadMono = Mono.just(DefaultPayload.create(Objects.requireNonNull(payload)));
      payload.release();
    }
    return this;
  }

  public SocketConnector keepAlive(Duration interval, Duration maxLifeTime) {
    if (!interval.negated().isNegative()) {
      throw new IllegalArgumentException("心跳间隔必须大于0");
    }
    if (!maxLifeTime.negated().isNegative()) {
      throw new IllegalArgumentException("心跳最大生命周期必须大于0");
    }
    this.keepAliveInterval = interval;
    this.keepAliveMaxLifeTime = maxLifeTime;
    return this;
  }

  public SocketConnector payloadDecoder(SocketPayloadDecoder payloadDecoder) {
    Objects.requireNonNull(payloadDecoder);
    this.payloadDecoder = payloadDecoder;
    return this;
  }

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
