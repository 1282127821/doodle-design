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
package io.rsocket.frame;

import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;
import io.rsocket.util.ByteBufPayload;

public class ZeroCopySocketPayloadDecoder implements SocketPayloadDecoder {

  @Override
  public Payload apply(ByteBuf byteBuf) {
    ByteBuf data;
    ByteBuf metadata;
    SocketFrameType frameType = SocketFrameHeaderCodec.frameType(byteBuf);
    if (frameType == SocketFrameType.ONEWAY) {
      data = SocketOnewayFrameCodec.data(byteBuf);
      metadata = SocketOnewayFrameCodec.metadata(byteBuf);
    } else {
      throw new IllegalArgumentException("当前暂不支持的协议类型: " + frameType);
    }
    return ByteBufPayload.create(data.retain(), metadata != null ? metadata.retain() : null);
  }
}
