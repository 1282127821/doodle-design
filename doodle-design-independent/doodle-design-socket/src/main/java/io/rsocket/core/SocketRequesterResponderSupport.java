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

import io.netty.buffer.ByteBufAllocator;
import io.rsocket.SocketConnection;
import io.rsocket.frame.decoder.PayloadDecoder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketRequesterResponderSupport {
  int mtu;
  int maxFrameLength;
  SocketConnection socketConnection;
  PayloadDecoder payloadDecoder;
  ByteBufAllocator allocator;

  public SocketRequesterResponderSupport(
      int mtu,
      int maxFrameLength,
      SocketConnection socketConnection,
      PayloadDecoder payloadDecoder) {
    this.mtu = mtu;
    this.maxFrameLength = maxFrameLength;
    this.socketConnection = socketConnection;
    this.payloadDecoder = payloadDecoder;
    this.allocator = socketConnection.alloc();
  }
}