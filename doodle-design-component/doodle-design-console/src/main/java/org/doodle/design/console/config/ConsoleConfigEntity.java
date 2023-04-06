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
package org.doodle.design.console.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import lombok.Data;
import org.springframework.data.annotation.Id;

/** Console 配置实体 */
@Data
public class ConsoleConfigEntity {

  /** 数据库存储主键ID, 客户端不需要所以忽略 Json */
  @JsonIgnore @Id private String id;

  private String dataId;
  private String group;
  private String configId;

  /** SpringBoot Environment 格式配置 */
  private Map<String, Object> configs;
}
