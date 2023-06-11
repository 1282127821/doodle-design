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
package org.doodle.design.payment.model.payload.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.doodle.design.payment.model.info.PaymentOrderDeliverRouteInfo;

@Builder
@ToString
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class JgPaymentOrderCreateRequest {
  // 订单UID
  @JsonAlias("order_id")
  String orderId;
  // 公会名称
  @JsonAlias("party_name")
  String partyName;
  // 单价
  @JsonAlias("per_price")
  int perPrice;
  // 商品描述
  @JsonAlias("product_desc")
  String productDesc;
  // 商品UID
  @JsonAlias("product_id")
  String productId;
  // 商品名称
  @JsonAlias("product_name")
  String productName;

  int ratio;

  @JsonAlias("remain_coin_num")
  int remainCoinNum;
  // 角色UID
  @JsonAlias("role_id")
  String roleId;
  // 角色等级
  @JsonAlias("role_level")
  int roleLevel;
  // 据色昵称
  @JsonAlias("role_name")
  String roleName;
  // 区服UID
  @JsonAlias("server_id")
  String serverId;
  // 区服名称
  @JsonAlias("server_name")
  String serverName;
  // 总计价格
  @JsonAlias("total_price")
  int totalPrice;

  String vip;
  // 兑现路由
  PaymentOrderDeliverRouteInfo route;
}
