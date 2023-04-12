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
import lombok.*;
import org.doodle.design.common.data.BaseDateEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = LoginRoleEntity.TABLE_OR_COLLECTION)
@Document(collection = LoginRoleEntity.TABLE_OR_COLLECTION)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class LoginRoleEntity extends BaseDateEntity<Long> {
  public static final String TABLE_OR_COLLECTION = "login_role";

  @Field(name = "role_id")
  @Column(name = "role_id", nullable = false, unique = true, updatable = false)
  private Long roleId;

  @NotEmpty(message = "角色名不能为空")
  private String name;

  @Min(value = 0, message = "角色等级最小为0")
  private int level;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(targetEntity = LoginAccountEntity.class, optional = false)
  @JoinColumn(name = "account_id")
  @DocumentReference(collection = LoginAccountEntity.TABLE_OR_COLLECTION)
  private LoginAccountEntity account;
}
