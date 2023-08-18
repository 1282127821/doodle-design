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
package org.doodle.design.payment;

import org.doodle.design.common.ProtoMapper;
import org.doodle.design.common.util.ProtoUtils;

public abstract class PaymentMapper implements ProtoMapper {

  public org.doodle.design.payment.model.info.PaymentOrderInfo fromProto(
      PaymentOrderInfo orderInfo) {
    return org.doodle.design.payment.model.info.PaymentOrderInfo.builder()
        .orderId(orderInfo.getOrderId())
        .accountId(orderInfo.getAccountId())
        .roleId(orderInfo.getRoleId())
        .roleName(orderInfo.getRoleName())
        .serverId(orderInfo.getServerId())
        .serverName(orderInfo.getServerName())
        .amount(orderInfo.getAmount())
        .productId(orderInfo.getProductId())
        .productName(orderInfo.getProductName())
        .productPrice(orderInfo.getProductPrice())
        .ext(ProtoUtils.fromProto(orderInfo.getExt()))
        .build();
  }

  public org.doodle.design.payment.model.payload.request.PaymentOrderDeliverRequest fromProto(
      PaymentOrderDeliverRequest request) {
    return org.doodle.design.payment.model.payload.request.PaymentOrderDeliverRequest.builder()
        .orderInfo(fromProto(request.getOrderInfo()))
        .build();
  }
}
