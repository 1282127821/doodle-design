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
package org.doodle.design.config;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = ConfigInstanceEntity.TABLE_OR_COLLECTION)
@Document(collection = ConfigInstanceEntity.TABLE_OR_COLLECTION)
public class ConfigInstanceEntity extends ConfigBaseEntity {
  public static final String TABLE_OR_COLLECTION = "config_instance";

  @OneToMany(targetEntity = ConfigSharedEntity.class)
  @DocumentReference(collection = ConfigSharedEntity.TABLE_OR_COLLECTION)
  @Column(name = "shared_properties")
  private List<ConfigSharedEntity> sharedProperties;
}
