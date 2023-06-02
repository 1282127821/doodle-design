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
import org.doodle.design.common.Result;
import org.doodle.design.common.Status;
import org.doodle.design.common.util.ProtoUtils;
import org.doodle.design.payment.model.dto.OrderInfoDto;

public class PaymentMapper implements ProtoMapper {

  public Result<org.doodle.design.payment.model.payload.reply.OrderCreateReply> fromProto(
      OrderCreateReply reply) {
    switch (reply.getResultCase()) {
      case ERROR -> {
        return ProtoUtils.fromProto(reply.getError());
      }
      case REPLY -> {
        return Result.ok(
            org.doodle.design.payment.model.payload.reply.OrderCreateReply.builder()
                .orderInfo(fromProto(reply.getReply()))
                .build());
      }
      default -> {
        return Result.bad();
      }
    }
  }

  public OrderCreateRequest toProto(
      org.doodle.design.payment.model.payload.request.OrderCreateRequest request) {
    return OrderCreateRequest.newBuilder()
        .setSdkType(request.getSdkType())
        .setAccountId(request.getAccountId())
        .setRoleId(request.getRoleId())
        .setExtraParams(toProto(request.getExtraParams()))
        .build();
  }

  public Result<Void> fromProto(OrderDeliverReply reply) {
    Status status = reply.getResult();
    return Result.code(status.getCode(), status.getMessage()).body(null);
  }

  public OrderDeliverReply fromProto(Status status) {
    return OrderDeliverReply.newBuilder().setResult(status).build();
  }

  public OrderInfoDto fromProto(OrderInfo info) {
    return OrderInfoDto.builder()
        .orderId(info.getOrderId())
        .orderStatus(info.getOrderStatus())
        .accountId(info.getAccountId())
        .roleId(info.getRoleId())
        .sdkBundle(fromProto(info.getSdkBundle()))
        .extraParams(fromProto(info.getExtraParams()))
        .build();
  }

  public OrderInfo toProto(OrderInfoDto dto) {
    return OrderInfo.newBuilder()
        .setOrderId(dto.getOrderId())
        .setOrderStatus(dto.getOrderStatus())
        .setAccountId(dto.getAccountId())
        .setRoleId(dto.getRoleId())
        .setSdkBundle(toProto(dto.getSdkBundle()))
        .setExtraParams(toProto(dto.getExtraParams()))
        .build();
  }
}
