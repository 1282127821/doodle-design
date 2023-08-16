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

import io.rsocket.Payload;
import io.rsocket.Socket;
import io.rsocket.SocketAcceptor;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.doodle.design.messaging.packet.reactive.PacketMappingMessageHandler;
import reactor.core.publisher.Mono;

@Slf4j
public class SocketMessageHandler extends PacketMappingMessageHandler {

  public SocketAcceptor serverAcceptor() {
    return (setupPayload, sendingSocket) -> {
      sendingSocket
          .oneway(DefaultPayload.create("hi"))
          .doOnNext(p -> log.info("给客户端发送 hi 消息"))
          .subscribe();
      return Mono.just(
          new Socket() {

            @Override
            public Mono<Void> oneway(Payload payload) {
              log.info(
                  "收到来自客户端的消息: {}", DefaultPayload.create(payload.data().retain()).getDataUtf8());
              return Mono.never();
            }
          });
    };
  }
}
