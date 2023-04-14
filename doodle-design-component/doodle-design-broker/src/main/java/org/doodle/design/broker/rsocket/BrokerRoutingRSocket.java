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
package org.doodle.design.broker.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import lombok.AllArgsConstructor;
import org.doodle.broker.design.frame.Address;
import org.doodle.broker.design.frame.BrokerFrame;
import org.doodle.design.broker.frame.BrokerFrameExtractor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
final class BrokerRoutingRSocket implements RSocket {
  private final BrokerRSocketLocator locator;
  private final BrokerFrameExtractor frameExtractor;

  @Override
  public Mono<Void> fireAndForget(Payload payload) {
    try {
      RSocket located = locate(payload);
      return located.fireAndForget(payload);
    } catch (Throwable t) {
      payload.release();
      return Mono.error(t);
    }
  }

  @Override
  public Mono<Payload> requestResponse(Payload payload) {
    try {
      RSocket located = locate(payload);
      return located.requestResponse(payload);
    } catch (Throwable t) {
      payload.release();
      return Mono.error(t);
    }
  }

  @Override
  public Flux<Payload> requestStream(Payload payload) {
    try {
      RSocket located = locate(payload);
      return located.requestStream(payload);
    } catch (Throwable t) {
      payload.release();
      return Flux.error(t);
    }
  }

  @Override
  public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
    return Flux.from(payloads)
        .switchOnFirst(
            (first, flux) -> {
              if (first.hasValue()) {
                Payload payload = first.get();
                try {
                  RSocket located = locate(payload);
                  return located.requestChannel(flux);
                } catch (Throwable t) {
                  payload.release();
                  return Flux.error(t);
                }
              }
              return flux;
            });
  }

  @Override
  public Mono<Void> metadataPush(Payload payload) {
    try {
      RSocket located = locate(payload);
      return located.metadataPush(payload);
    } catch (Throwable t) {
      payload.release();
      return Mono.error(t);
    }
  }

  private RSocket locate(Payload payload) {
    BrokerFrame brokerFrame = frameExtractor.apply(payload);
    if (brokerFrame.getKindCase() != BrokerFrame.KindCase.ADDRESS) {
      throw new IllegalStateException("必须传入 Address 参数");
    }
    Address address = brokerFrame.getAddress();

    if (!locator.supports(address.getRoutingType())) {
      throw new IllegalStateException("找不到对应路由类型的 RSocketLocator: " + address.getRoutingType());
    }
    return locator.locate(address);
  }
}
