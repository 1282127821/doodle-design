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
package org.doodle.design.payment.model.dto;

import java.util.Map;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.doodle.design.common.model.SdkBundle;
import org.doodle.design.payment.OrderInfo;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoDto {
  String orderId;
  OrderInfo.OrderStatus orderStatus = OrderInfo.OrderStatus.UNRECOGNIZED;
  String accountId;
  String roleId;
  SdkBundle sdkBundle;
  Map<String, Object> extraParams;
}
