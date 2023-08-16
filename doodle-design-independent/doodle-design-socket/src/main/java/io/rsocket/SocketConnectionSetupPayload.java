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
package io.rsocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.rsocket.frame.SocketFrameHeaderCodec;
import io.rsocket.frame.SocketSetupFrameCodec;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SocketConnectionSetupPayload extends ConnectionSetupPayload {
  ByteBuf setupFrame;

  @Override
  public String metadataMimeType() {
    return null;
  }

  @Override
  public String dataMimeType() {
    return null;
  }

  @Override
  public int keepAliveInterval() {
    return SocketSetupFrameCodec.keepAliveInterval(setupFrame);
  }

  @Override
  public int keepAliveMaxLifetime() {
    return SocketSetupFrameCodec.keepAliveMaxLifetime(setupFrame);
  }

  @Override
  public int getFlags() {
    return SocketFrameHeaderCodec.flags(setupFrame);
  }

  @Override
  public boolean willClientHonorLease() {
    return false;
  }

  @Override
  public boolean isResumeEnabled() {
    return false;
  }

  @Override
  public ByteBuf resumeToken() {
    return null;
  }

  @Override
  public SocketConnectionSetupPayload touch() {
    setupFrame.touch();
    return this;
  }

  @Override
  protected void deallocate() {
    setupFrame.release();
  }

  @Override
  public boolean hasMetadata() {
    return SocketFrameHeaderCodec.hasMetadata(setupFrame);
  }

  @Override
  public ByteBuf sliceMetadata() {
    final ByteBuf metadata = SocketSetupFrameCodec.metadata(setupFrame);
    return metadata == null ? Unpooled.EMPTY_BUFFER : metadata;
  }

  @Override
  public ByteBuf sliceData() {
    return SocketSetupFrameCodec.data(setupFrame);
  }

  @Override
  public ByteBuf data() {
    return sliceData();
  }

  @Override
  public ByteBuf metadata() {
    return sliceMetadata();
  }

  @Override
  public SocketConnectionSetupPayload touch(Object hint) {
    setupFrame.touch(hint);
    return this;
  }
}
