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
package org.doodle.design.common.data;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@ToString(callSuper = true)
@Getter
@Setter
@MappedSuperclass
public abstract class BaseDateEntity<ID> extends BaseEntity<ID> {

  @CreatedDate
  @Column(name = "created_date", updatable = false, nullable = false)
  protected LocalDateTime createdDate;

  @LastModifiedDate
  @Column(name = "modified_date", nullable = false)
  protected LocalDateTime modifiedDate;
}