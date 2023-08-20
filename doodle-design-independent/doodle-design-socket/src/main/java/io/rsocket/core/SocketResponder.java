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
import io.rsocket.frame.SocketFrameHeaderCodec;
import io.rsocket.frame.SocketFrameType;
import io.rsocket.frame.decoder.PayloadDecoder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketResponder extends SocketRequesterResponderSupport implements Socket {
  Socket socketHandler;

  public SocketResponder(
      int maxFrameLength,
      SocketConnection socketConnection,
      Socket socketHandler,
      PayloadDecoder payloadDecoder) {
    super(maxFrameLength, socketConnection, payloadDecoder);
    this.socketHandler = socketHandler;

    socketConnection.receive().subscribe(this::handleFrames, e -> {});
    socketConnection
        .onClose()
        .subscribe(null, this::tryTerminateOnConnectionError, this::tryTerminateOnConnectionClose);
  }

  private void handleFrames(ByteBuf frame) {
    try {
      SocketFrameType frameType = SocketFrameHeaderCodec.frameType(frame);
      if (frameType == SocketFrameType.ONEWAY) {
        handleOneWay(frame);
      } else {
        log.error("未支持的协议类型: {}", frameType);
      }
    } catch (Throwable t) {
      log.error("处理协议发生未知错误", t);
      tryTerminateOnConnectionError(t);
    }
  }

  private void handleOneWay(ByteBuf frame) {
    oneway(super.getPayloadDecoder().apply(frame))
        .subscribe(SocketOnewayResponderSubscriber.INSTANCE);
  }

  private void tryTerminateOnConnectionClose() {}

  private void tryTerminateOnConnectionError(Throwable e) {}

  @Override
  public Mono<Void> oneway(Payload payload) {
    try {
      return socketHandler.oneway(payload);
    } catch (Throwable t) {
      return Mono.error(t);
    }
  }

  @Override
  public void dispose() {}

  @Override
  public boolean isDisposed() {
    return getSocketConnection().isDisposed();
  }

  @Override
  public Mono<Void> onClose() {
    return getSocketConnection().onClose();
  }
}
