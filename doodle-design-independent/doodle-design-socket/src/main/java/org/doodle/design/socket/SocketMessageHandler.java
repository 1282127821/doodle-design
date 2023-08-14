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

import lombok.extern.slf4j.Slf4j;
import org.doodle.design.messaging.packet.reactive.PacketMappingMessageHandler;

@Slf4j
public class SocketMessageHandler extends PacketMappingMessageHandler {

  public SocketServerAcceptor serverAcceptor() {
    return (setupPayload, socketConnection) ->
        socketConnection
            .onClose()
            .doFirst(
                () -> {
                  log.info(
                      "连接建立: ID: {} IP: {}",
                      socketConnection.connection().channel().id().asShortText(),
                      socketConnection.remoteAddress());

                  socketConnection.connection().onReadIdle(5000, socketConnection::dispose);
                })
            .doOnTerminate(
                () ->
                    log.info(
                        "连接关闭: ID: {}",
                        socketConnection.connection().channel().id().asShortText()));
  }
}
