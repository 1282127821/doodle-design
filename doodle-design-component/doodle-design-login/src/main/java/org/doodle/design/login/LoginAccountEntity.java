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

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.*;
import org.doodle.design.common.data.BaseDateEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = LoginAccountEntity.TABLE_OR_COLLECTION)
@Document(collection = LoginAccountEntity.TABLE_OR_COLLECTION)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class LoginAccountEntity extends BaseDateEntity<Long> {
  public static final String TABLE_OR_COLLECTION = "login_account";

  @Min(value = 0L, message = "帐号ID不能小于0")
  @Column(name = "account_id", nullable = false, unique = true, updatable = false)
  private Long accountId;

  @NotEmpty(message = "帐号不能为空")
  @Column(nullable = false, unique = true, updatable = false)
  private String account;

  @NotEmpty(message = "密码不能为空")
  @Column(nullable = false)
  private String password;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
  @DocumentReference(collection = LoginAccountEntity.TABLE_OR_COLLECTION)
  private List<LoginRoleEntity> roles;
}
