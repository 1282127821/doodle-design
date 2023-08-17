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
package org.doodle.design.socket.reactive;

import io.rsocket.Socket;
import io.rsocket.SocketAcceptor;
import io.rsocket.SocketConnectionSetupPayload;
import java.lang.reflect.AnnotatedElement;
import lombok.extern.slf4j.Slf4j;
import org.doodle.design.messaging.packet.PacketMapping;
import org.doodle.design.messaging.packet.reactive.PacketMappingMessageHandler;
import org.doodle.design.socket.MessagingSocket;
import org.doodle.design.socket.SocketConnectMapping;
import org.doodle.design.socket.SocketFrameTypeMessageCondition;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.CompositeMessageCondition;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import reactor.core.publisher.Mono;

@Slf4j
public class SocketMessageHandler extends PacketMappingMessageHandler {

  @Nullable
  @Override
  protected CompositeMessageCondition getCondition(AnnotatedElement element) {
    PacketMapping packetMapping =
        AnnotatedElementUtils.findMergedAnnotation(element, PacketMapping.class);
    if (packetMapping != null) {
      return new CompositeMessageCondition(
          SocketFrameTypeMessageCondition.EMPTY_CONDITION,
          new DestinationPatternsMessageCondition(
              new String[] {String.valueOf(packetMapping.value())}, obtainRouteMatcher()));
    }
    SocketConnectMapping connectMapping =
        AnnotatedElementUtils.findMergedAnnotation(element, SocketConnectMapping.class);
    if (connectMapping != null) {
      return new CompositeMessageCondition(
          SocketFrameTypeMessageCondition.CONNECT_CONDITION,
          new DestinationPatternsMessageCondition());
    }
    return null;
  }

  public SocketAcceptor serverAcceptor() {
    return (setupPayload, sendingSocket) -> {
      MessagingSocket responder;
      try {
        responder = createResponder(setupPayload, sendingSocket);
      } catch (Throwable ex) {
        return Mono.error(ex);
      }
      return responder.handleConnectionSetupPayload(setupPayload).then(Mono.just(responder));
    };
  }

  private MessagingSocket createResponder(
      SocketConnectionSetupPayload setupPayload, Socket socket) {
    return new MessagingSocket(this);
  }
}
