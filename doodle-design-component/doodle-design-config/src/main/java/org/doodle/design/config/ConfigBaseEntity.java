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

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.doodle.design.common.data.BaseSecurityEntity;
import org.doodle.design.common.data.jpa.JpaConverters;

@ToString(callSuper = true)
@Getter
@Setter
@MappedSuperclass
public abstract class ConfigBaseEntity extends BaseSecurityEntity<String> {

  @NotEmpty(message = "配置数据集(dataId)不能为空")
  @Column(nullable = false)
  protected String dataId;

  @NotEmpty(message = "配置分组(group)不能为空")
  @Column(nullable = false)
  protected String group;

  @NotEmpty(message = "配置ID(configId)不能为空")
  @Column(name = "config_id", nullable = false)
  protected String configId;

  @NotEmpty(message = "配置参数(configProperties)不能为空")
  @Column(name = "config_properties", nullable = false)
  @Convert(converter = JpaConverters.ObjectToJsonConverter.class)
  protected Map<String, Object> configProperties;
}
