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

import io.rsocket.RSocket;
import java.util.List;
import lombok.AllArgsConstructor;
import org.doodle.broker.design.frame.Address;
import org.doodle.broker.design.frame.RoutingType;

@AllArgsConstructor
public class MulticastBrokerRSocketLocator implements BrokerRSocketLocator {
  private final BrokerRSocketQuery query;

  @Override
  public boolean supports(RoutingType routingType) {
    return routingType == RoutingType.MULTICAST;
  }

  @Override
  public RSocket locate(Address address) {
    List<RSocket> found = query.query(address.getTags());
    return null;
  }
}
