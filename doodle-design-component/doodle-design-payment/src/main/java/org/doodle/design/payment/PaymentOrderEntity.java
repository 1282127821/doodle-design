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

import jakarta.validation.constraints.*;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import lombok.*;
import org.doodle.design.common.data.BaseDateEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = PaymentOrderEntity.COLLECTION)
public class PaymentOrderEntity extends BaseDateEntity<String> {
  public static final String COLLECTION = "payment_order";

  @NotBlank(message = "玩家帐号ID不能为空")
  private String accountId;

  @NotBlank(message = "玩家角色ID不能为空")
  private String roleId;

  @NotBlank(message = "玩家所在服ID不能为空")
  private String serverId;

  @NotBlank(message = "玩家渠道ID不能为空")
  private String channelId;

  @NotBlank(message = "玩家游戏ID不能为空")
  private String gameId;

  @Positive(message = "玩家支付配置ID不能为负数或零")
  private int configId;

  @Positive(message = "玩家支付金额不能为负数或零")
  private double amount;

  @NotNull(message = "玩家支付货币单位不能为空")
  private Currency currency = Currency.getInstance(Locale.CHINA);

  @NotNull(message = "玩家支付订单状态不能为空")
  private PaymentOrderStatus status = PaymentOrderStatus.CREATED;

  private Map<String, Object> params;
}