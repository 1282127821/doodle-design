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
import io.netty.buffer.ByteBufAllocator;
import lombok.experimental.UtilityClass;
import reactor.util.annotation.Nullable;

@UtilityClass
public final class SocketOnewayFrameCodec {

  public static ByteBuf encode(
      ByteBufAllocator allocator, @Nullable ByteBuf metadata, ByteBuf data) {
    return SocketGenericFrameCodec.encode(allocator, SocketFrameType.ONEWAY, metadata, data);
  }

  public static ByteBuf data(ByteBuf byteBuf) {
    return SocketGenericFrameCodec.data(byteBuf);
  }

  @Nullable
  public static ByteBuf metadata(ByteBuf byteBuf) {
    return SocketGenericFrameCodec.metadata(byteBuf);
  }
}