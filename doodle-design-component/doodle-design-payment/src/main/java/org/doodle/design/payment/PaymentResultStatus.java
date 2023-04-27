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

import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;

@Getter
public enum PaymentResultStatus {
  ORDER_CREATE_FAILED(-2, "订单创建失败"),
  ORDER_NOT_FOUND(-3, "查找不到对应订单"),
  SDK_DELIVER_FAILED(-4, "订单兑现失败"),
  EXCEPTION(-5, "异常错误"),
  RESERVED(-100, "");

  final int status;
  final String message;

  PaymentResultStatus(int status, String message) {
    this.status = status;
    this.message = message;
  }

  public static PaymentResultStatus valueOf(int status) {
    return Arrays.stream(values()).filter(v -> v.status == status).findFirst().orElse(RESERVED);
  }

  public static String message(int status) {
    PaymentResultStatus resultStatus = valueOf(status);
    return Objects.nonNull(resultStatus) ? resultStatus.message : RESERVED.message;
  }
}
