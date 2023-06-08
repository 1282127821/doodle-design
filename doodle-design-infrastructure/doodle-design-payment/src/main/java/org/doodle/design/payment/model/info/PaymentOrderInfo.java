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
package org.doodle.design.payment.model.info;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@ToString
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrderInfo {
  // 支付订单UID
  String orderId;
  // 帐号UID
  String accountId;
  // 角色UID
  String roleId;
  // 角色昵称
  String roleName;
  // 区服UID
  String serverId;
  // 区服名称
  String serverName;
  // 支付金额
  String amount;
  // 商品UID
  String productId;
  // 商品名称
  String productName;
  // 商品价格
  String productPrice;
}
