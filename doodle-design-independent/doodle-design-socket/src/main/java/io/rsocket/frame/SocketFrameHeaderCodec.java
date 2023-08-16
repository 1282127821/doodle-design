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

@UtilityClass
public final class SocketFrameHeaderCodec {

  /** (M)etadata flag: 标识是否包含元数据 */
  public static final int FLAGS_M = 0b0000_1000;

  private static final int FRAME_TYPE_BITS = 4;
  private static final int FRAME_TYPE_SHIFT = Byte.SIZE - FRAME_TYPE_BITS;

  public static final int FRAME_HEADER_SIZE = Byte.BYTES;

  public static ByteBuf encode(ByteBufAllocator allocator, SocketFrameType frameType, int flags) {
    byte typeAndFlags = (byte) (frameType.getEncodedType() << FRAME_TYPE_SHIFT | (byte) flags);
    return allocator.buffer().writeByte(typeAndFlags);
  }

  public static int flags(ByteBuf byteBuf) {
    byteBuf.markReaderIndex();
    int typeAndFlags = byteBuf.readByte() & 0xFF;
    byteBuf.resetReaderIndex();
    return typeAndFlags;
  }

  public static SocketFrameType frameType(ByteBuf byteBuf) {
    return SocketFrameType.fromEncodedType(flags(byteBuf) >> FRAME_TYPE_SHIFT);
  }

  public static boolean hasMetadata(ByteBuf byteBuf) {
    return (flags(byteBuf) & FLAGS_M) == FLAGS_M;
  }
}
