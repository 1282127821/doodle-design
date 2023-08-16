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
package org.doodle.design.socket.frame;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.rsocket.Payload;
import io.rsocket.frame.SocketFrameBodyCodec;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class SocketSetupFrameCodec {

  public static final int KEEP_ALIVE_INTERVAL_OFFSET = Byte.BYTES;
  public static final int KEEP_ALIVE_MAX_LIFETIME_OFFSET =
      KEEP_ALIVE_INTERVAL_OFFSET + Integer.BYTES;

  public static ByteBuf encode(
      ByteBufAllocator allocator,
      Payload setupPayload,
      int keepAliveInterval,
      int keepAliveMaxLifeTime) {
    ByteBuf data = setupPayload.sliceData();
    boolean hasMetadata = setupPayload.hasMetadata();
    ByteBuf metadata = hasMetadata ? setupPayload.sliceMetadata() : null;

    int flags = 0;

    if (hasMetadata) {
      flags |= SocketFrameHeaderCodec.FLAGS_M;
    }

    ByteBuf header = SocketFrameHeaderCodec.encode(allocator, SocketFrameType.SETUP, flags);
    header.writeInt(keepAliveInterval).writeInt(keepAliveMaxLifeTime);

    return SocketFrameBodyCodec.encode(allocator, header, metadata, hasMetadata, data);
  }

  public static int keepAliveInterval(ByteBuf byteBuf) {
    byteBuf.markReaderIndex();
    int keepAliveInterval = byteBuf.skipBytes(KEEP_ALIVE_INTERVAL_OFFSET).readInt();
    byteBuf.resetReaderIndex();
    return keepAliveInterval;
  }

  public static int keepAliveMaxLifetime(ByteBuf byteBuf) {
    byteBuf.markReaderIndex();
    int keepAliveMaxLifetime = byteBuf.skipBytes(KEEP_ALIVE_MAX_LIFETIME_OFFSET).readInt();
    byteBuf.resetReaderIndex();
    return keepAliveMaxLifetime;
  }

  public static ByteBuf metadata(ByteBuf byteBuf) {
    boolean hasMetadata = SocketFrameHeaderCodec.hasMetadata(byteBuf);
    if (!hasMetadata) {
      return null;
    }
    byteBuf.markReaderIndex();
    // TODO: 2023/8/16 读取 metadata
    byteBuf.resetReaderIndex();
    return null;
  }
}
