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
import io.rsocket.SocketAcceptorFunction;
import io.rsocket.SocketConnectionSetupPayload;
import io.rsocket.metadata.WellKnownMimeType;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.doodle.design.messaging.packet.PacketMapping;
import org.doodle.design.messaging.packet.reactive.PacketMappingMessageHandler;
import org.doodle.design.socket.MessagingSocket;
import org.doodle.design.socket.SocketConnectMapping;
import org.doodle.design.socket.SocketFrameTypeMessageCondition;
import org.doodle.design.socket.SocketStrategies;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.codec.Encoder;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.CompositeMessageCondition;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocketMessageHandler extends PacketMappingMessageHandler {
  final List<Encoder<?>> encoders = new ArrayList<>();
  SocketStrategies strategies;
  @Setter MimeType defaultDataMimeType = MimeTypeUtils.APPLICATION_JSON;

  @Setter
  MimeType defaultMetadataMimeType =
      MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString());

  public void setEncoders(List<Encoder<?>> encoders) {
    this.encoders.clear();
    this.encoders.addAll(encoders);
    this.strategies =
        this.strategies
            .mutate()
            .encoders(
                list -> {
                  list.clear();
                  list.addAll(encoders);
                })
            .build();
  }

  public void setStrategies(SocketStrategies strategies) {
    this.strategies = strategies;
    setEncoders(strategies.encoders());
    super.setDecoders(strategies.decoders());
    super.setRouteMatcher(strategies.routeMatcher());
    super.setReactiveAdapterRegistry(strategies.reactiveAdapterRegistry());
  }

  @Override
  public void afterPropertiesSet() {
    getArgumentResolverConfigurer().addCustomResolver(new SocketRequesterMethodArgumentResolver());
    getReturnValueHandlerConfigurer().addCustomHandler(new SocketPayloadReturnValueHandler());
    super.afterPropertiesSet();
  }

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

  public SocketAcceptorFunction serverAcceptor() {
    return (setupPayload, sendingSocket) -> {
      MessagingSocket responder;
      try {
        responder = createResponder(setupPayload, sendingSocket);
      } catch (Throwable ex) {
        log.error("Socket建立连接发生错误", ex);
        return Mono.error(ex);
      }
      return responder.handleConnectionSetupPayload(setupPayload).then(Mono.just(responder));
    };
  }

  private MessagingSocket createResponder(
      SocketConnectionSetupPayload setupPayload, Socket socket) {
    String mimeType = setupPayload.dataMimeType();
    MimeType dataMimeType =
        StringUtils.hasText(mimeType)
            ? MimeTypeUtils.parseMimeType(mimeType)
            : this.defaultDataMimeType;
    Assert.notNull(dataMimeType, "Setup协议没有发送 dataMimeType, 并且没有设置默认值");
    Assert.isTrue(isDataMimeTypeSupported(dataMimeType), "暂不支持数据类型: " + dataMimeType);
    mimeType = setupPayload.metadataMimeType();
    MimeType metadataMimeType =
        StringUtils.hasText(mimeType)
            ? MimeTypeUtils.parseMimeType(mimeType)
            : this.defaultMetadataMimeType;
    Assert.notNull(metadataMimeType, "Setup协议没有发送 metadataMimeType, 并且没有设置默认值");
    return new MessagingSocket(
        socket, dataMimeType, metadataMimeType, this, obtainRouteMatcher(), this.strategies);
  }

  private boolean isDataMimeTypeSupported(MimeType dataMimeType) {
    for (Encoder<?> encoder : this.encoders) {
      for (MimeType encodable : encoder.getEncodableMimeTypes()) {
        if (encodable.isCompatibleWith(dataMimeType)) {
          return true;
        }
      }
    }
    return false;
  }
}
