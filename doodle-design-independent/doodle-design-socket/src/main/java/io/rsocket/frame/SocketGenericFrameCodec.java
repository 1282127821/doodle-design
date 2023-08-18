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
public final class SocketGenericFrameCodec {

  static ByteBuf encode(
      final ByteBufAllocator allocator,
      final SocketFrameType frameType,
      @Nullable ByteBuf metadata,
      ByteBuf data) {
    final boolean hasMetadata = metadata != null;
    int flags = 0;

    if (hasMetadata) {
      flags |= SocketFrameHeaderCodec.FLAGS_M;
    }

    final ByteBuf header = SocketFrameHeaderCodec.encode(allocator, frameType, flags);
    return FrameBodyCodec.encode(allocator, header, metadata, hasMetadata, data);
  }

  public static ByteBuf data(ByteBuf byteBuf) {
    boolean hasMetadata = SocketFrameHeaderCodec.hasMetadata(byteBuf);
    byteBuf.markReaderIndex();
    byteBuf.skipBytes(SocketFrameHeaderCodec.FRAME_HEADER_SIZE);
    ByteBuf data = FrameBodyCodec.dataWithoutMarking(byteBuf, hasMetadata);
    byteBuf.resetReaderIndex();
    return data;
  }

  @Nullable
  public static ByteBuf metadata(ByteBuf byteBuf) {
    boolean hasMetadata = SocketFrameHeaderCodec.hasMetadata(byteBuf);
    if (!hasMetadata) {
      return null;
    }
    byteBuf.markReaderIndex();
    ByteBuf metadata = FrameBodyCodec.metadataWithoutMarking(byteBuf);
    byteBuf.resetReaderIndex();
    return metadata;
  }
}