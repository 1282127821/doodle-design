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

/** Broker Protobuf 路由协议辅助方法 */
@UtilityClass
public final class BrokerFrameUtils {

  /**
   * 创建 setup 协议
   *
   * @param tags 路由索引 tag 键值对
   * @return
   */
  public static BrokerFrame setup(Map<String, String> tags) {
    return setup(tags, java.util.UUID.randomUUID().toString());
  }

  /**
   * 创建 setup 协议
   *
   * @param tags 路由索引 tag 键值对
   * @param routeId 路由唯一ID
   * @return
   */
  public static BrokerFrame setup(Map<String, String> tags, String routeId) {
    RouteSetup.Builder setup =
        RouteSetup.newBuilder()
            .setRouteId(UUID.newBuilder().setId(routeId).build())
            .setTags(Tags.newBuilder().putAllTag(tags));
    return BrokerFrame.newBuilder().setSetup(setup).build();
  }

  /**
   * UNICAST address 协议, 交集 查询
   *
   * @param tags 目标服务路由索引 tag 键值对
   * @return
   */
  public static BrokerFrame unicast(Map<String, String> tags) {
    return unicast(tags, QueryType.AND);
  }

  /**
   * UNICAST address 协议
   *
   * @param tags 目标服务路由索引 tag 键值对
   * @param queryType 集合查询操作类型
   * @return
   */
  public static BrokerFrame unicast(Map<String, String> tags, QueryType queryType) {
    return address(tags, queryType, RoutingType.UNICAST);
  }

  /**
   * MULTICAST address 协议, 交集 查询
   *
   * @param tags 目标服务路由索引 tag 键值对
   * @return
   */
  public static BrokerFrame multicast(Map<String, String> tags) {
    return multicast(tags, QueryType.AND);
  }

  /**
   * MULTICAST address 协议
   *
   * @param tags 目标服务路由索引 tag 键值对
   * @param queryType 集合查询操作类型
   * @return
   */
  public static BrokerFrame multicast(Map<String, String> tags, QueryType queryType) {
    return address(tags, queryType, RoutingType.MULTICAST);
  }

  /**
   * address 协议
   *
   * @param tags 目标服务路由索引 tag 键值对
   * @param queryType 集合查询操作类型
   * @param routingType 请求路由类型
   * @return
   */
  public static BrokerFrame address(
      Map<String, String> tags, QueryType queryType, RoutingType routingType) {
    Address.Builder address =
        Address.newBuilder()
            .setQueryType(queryType)
            .setTags(Tags.newBuilder().putAllTag(tags))
            .setRoutingType(routingType);
    return BrokerFrame.newBuilder().setAddress(address).build();
  }
}
