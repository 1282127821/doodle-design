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
package org.doodle.design.login;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;
import org.doodle.design.common.SdkType;
import org.doodle.design.common.data.BaseDateEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = AccountEntity.COLLECTION)
public class AccountEntity extends BaseDateEntity<String> {
  public static final String COLLECTION = "account";

  @NotNull(message = "SDK 类型不能为空")
  private SdkType type = SdkType.EMBEDDED;

  @Indexed
  @NotEmpty(message = "SDK 账号绑定不能为空")
  private String bind;

  private List<String> roles;
}