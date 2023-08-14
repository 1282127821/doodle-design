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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SocketFrameType {
  RESERVED(0x00),
  SETUP(0x01),
  KEEP_ALIVE(0x02),
  REQUEST_REPLY(0x03);

  private static final SocketFrameType[] FRAME_TYPES_BY_ENCODED_TYPE;

  static {
    FRAME_TYPES_BY_ENCODED_TYPE = new SocketFrameType[values().length + 1];

    for (SocketFrameType frameType : values()) {
      FRAME_TYPES_BY_ENCODED_TYPE[frameType.encodedType] = frameType;
    }
  }

  int encodedType;

  SocketFrameType(int encodedType) {
    this.encodedType = encodedType;
  }

  public static SocketFrameType fromEncodedType(int encodedType) {
    SocketFrameType frameType = FRAME_TYPES_BY_ENCODED_TYPE[encodedType];
    if (frameType == null) {
      throw new IllegalArgumentException(String.format("非法协议类型: %d", encodedType));
    }
    return frameType;
  }
}
