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
package org.doodle.design.broker.frame;

import java.util.Map;
import lombok.experimental.UtilityClass;
import org.doodle.broker.design.frame.*;

@UtilityClass
public final class BrokerFrameUtils {

  public static BrokerFrame setup(Map<String, String> tags) {
    RouteSetup.Builder setup = RouteSetup.newBuilder().setTags(Tags.newBuilder().putAllTag(tags));
    return BrokerFrame.newBuilder().setSetup(setup).build();
  }

  public static BrokerFrame unicast(Map<String, String> tags) {
    return address(tags, RoutingType.UNICAST);
  }

  public static BrokerFrame multicast(Map<String, String> tags) {
    return address(tags, RoutingType.MULTICAST);
  }

  public static BrokerFrame address(Map<String, String> tags, RoutingType type) {
    Address.Builder address =
        Address.newBuilder().setTags(Tags.newBuilder().putAllTag(tags)).setRoutingType(type);
    return BrokerFrame.newBuilder().setAddress(address).build();
  }
}
