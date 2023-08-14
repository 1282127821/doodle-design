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
import io.rsocket.frame.SocketFrameBodyCodec;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class SocketKeepAliveFrameCodec {

  public static ByteBuf encode(ByteBufAllocator allocator, ByteBuf data) {
    ByteBuf header = SocketFrameHeaderCodec.encode(allocator, SocketFrameType.KEEP_ALIVE, 0);
    return SocketFrameBodyCodec.encode(allocator, header, null, false, data);
  }
}
