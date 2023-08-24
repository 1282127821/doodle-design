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

import io.netty.buffer.PooledByteBufAllocator;
import io.rsocket.Payload;
import io.rsocket.Socket;
import io.rsocket.SocketConnectionSetupPayload;
import io.rsocket.frame.SocketFrameType;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.doodle.design.socket.reactive.SocketMessageHandler;
import org.doodle.design.socket.reactive.SocketRequesterMethodArgumentResolver;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.handler.invocation.reactive.HandlerMethodReturnValueHandler;
import org.springframework.messaging.rsocket.PayloadUtils;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MimeType;
import org.springframework.util.RouteMatcher;
import reactor.core.publisher.Mono;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class MessagingSocket implements Socket {
  Socket socket;
  MimeType dataMimeType;
  MimeType metadataMimeType;
  SocketMessageHandler messageHandler;
  RouteMatcher routeMatcher;
  SocketStrategies strategies;

  public Mono<Void> handleConnectionSetupPayload(SocketConnectionSetupPayload setupPayload) {
    setupPayload.retain();
    return handle(setupPayload, SocketFrameType.SETUP);
  }

  @Override
  public Mono<Void> oneway(Payload payload) {
    return handle(payload, SocketFrameType.ONEWAY);
  }

  private Mono<Void> handle(Payload payload, SocketFrameType frameType) {
    MessageHeaders headers = createHeaders(payload, frameType);
    DataBuffer dataBuffer = retainDataAndReleasePayload(payload);
    int refCnt = refCount(dataBuffer);
    Message<DataBuffer> message = MessageBuilder.createMessage(dataBuffer, headers);
    return Mono.defer(() -> this.messageHandler.handleMessage(message))
        .doFinally(
            s -> {
              if (refCount(dataBuffer) == refCnt) {
                DataBufferUtils.release(dataBuffer);
              }
            });
  }

  private MessageHeaders createHeaders(Payload payload, SocketFrameType frameType) {
    MessageHeaderAccessor headers = new MessageHeaderAccessor();
    headers.setLeaveMutable(true);
    SocketMetadataExtractor metadataExtractor = this.strategies.metadataExtractor();
    Map<String, Object> metadataValues = metadataExtractor.extract(payload, metadataMimeType);
    metadataValues.put(SocketMetadataExtractor.ROUTE_KEY, "");
    for (Map.Entry<String, Object> entry : metadataValues.entrySet()) {
      if (entry.getKey().equals(SocketMetadataExtractor.ROUTE_KEY)) {
        RouteMatcher.Route route = this.routeMatcher.parseRoute((String) entry.getValue());
        headers.setHeader(DestinationPatternsMessageCondition.LOOKUP_DESTINATION_HEADER, route);
      } else {
        headers.setHeader(entry.getKey(), entry.getValue());
      }
    }
    headers.setContentType(this.dataMimeType);
    headers.setHeader(SocketRequesterMethodArgumentResolver.SOCKET_REQUESTER_HEADER, socket);
    headers.setHeader(SocketFrameTypeMessageCondition.FRAME_TYPE_HEADER, frameType);
    headers.setHeader(
        HandlerMethodReturnValueHandler.DATA_BUFFER_FACTORY_HEADER,
        this.strategies.dataBufferFactory());
    return headers.getMessageHeaders();
  }

  private int refCount(DataBuffer dataBuffer) {
    return dataBuffer instanceof NettyDataBuffer
        ? ((NettyDataBuffer) dataBuffer).getNativeBuffer().refCnt()
        : 1;
  }

  private DataBuffer retainDataAndReleasePayload(Payload payload) {
    return PayloadUtils.retainDataAndReleasePayload(
        payload, new NettyDataBufferFactory(PooledByteBufAllocator.DEFAULT));
  }
}
