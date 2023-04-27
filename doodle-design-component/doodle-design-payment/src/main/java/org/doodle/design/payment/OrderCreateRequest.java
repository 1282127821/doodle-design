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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import lombok.Data;
import org.doodle.design.common.SdkType;

@Data
public class OrderCreateRequest {
  @NotBlank(message = "玩家帐号ID不能为空")
  private String accountId;

  @NotBlank(message = "玩家sdk账号不能为空")
  private String bind;

  @NotNull(message = "Sdk类型不能为空")
  private SdkType type = SdkType.EMBEDDED;

  @NotBlank(message = "玩家角色ID不能为空")
  private String roleId;

  @NotBlank(message = "玩家所在服ID不能为空")
  private String serverId;

  @NotBlank(message = "玩家渠道ID不能为空")
  private String channelId;

  @NotBlank(message = "玩家游戏ID不能为空")
  private String gameId;

  @Positive(message = "玩家支付金额不能为负数或零")
  private double amount;

  @NotNull(message = "玩家支付货币单位不能为空")
  private Currency currency = Currency.getInstance(Locale.CHINA);

  private Map<String, Object> params;
}
