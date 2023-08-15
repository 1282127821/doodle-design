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
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.rsocket.frame.decoder.PayloadDecoder;
import lombok.extern.slf4j.Slf4j;
import org.doodle.design.socket.Socket;
import org.doodle.design.socket.SocketRequesterResponderSupport;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;

@Slf4j
public class OnewayResponderSubscriber implements CoreSubscriber<Void>, ResponderFrameHandler {

  public static OnewayResponderSubscriber INSTANCE = new OnewayResponderSubscriber();

  final ByteBufAllocator allocator;
  final PayloadDecoder payloadDecoder;
  final SocketRequesterResponderSupport requesterResponderSupport;
  final Socket handler;
  final int maxInboundPayloadSize;

  CompositeByteBuf frames;

  OnewayResponderSubscriber() {
    this.allocator = null;
    this.payloadDecoder = null;
    this.maxInboundPayloadSize = 0;
    this.requesterResponderSupport = null;
    this.handler = null;
    this.frames = null;
  }

  @Override
  public void handleNext(ByteBuf frame, boolean hasFollows, boolean isLastPayload) {
    CompositeByteBuf frames = this.frames;
  }

  @Override
  public void handleCancel() {
    final CompositeByteBuf frames = this.frames;
    if (frames != null) {
      this.frames = null;
      frames.release();
    }
  }

  @Override
  public void onSubscribe(Subscription s) {
    s.request(Long.MAX_VALUE);
  }

  @Override
  public void onNext(Void unused) {}

  @Override
  public void onError(Throwable throwable) {}

  @Override
  public void onComplete() {}
}
