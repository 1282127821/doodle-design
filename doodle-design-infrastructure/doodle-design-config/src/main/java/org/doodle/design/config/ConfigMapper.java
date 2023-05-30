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

import org.doodle.design.common.ProtoMapper;
import org.doodle.design.config.model.dto.ConfigIdInfoDto;
import org.doodle.design.config.model.dto.ConfigPropsInfoDto;

public abstract class ConfigMapper implements ProtoMapper {

  public ConfigIdInfoDto fromProto(ConfigIdInfo info) {
    return ConfigIdInfoDto.builder()
        .group(info.getGroup())
        .dataId(info.getDataId())
        .profile(info.getProfile())
        .build();
  }

  public ConfigIdInfo toProto(ConfigIdInfoDto dto) {
    return ConfigIdInfo.newBuilder()
        .setGroup(dto.getGroup())
        .setDataId(dto.getDataId())
        .setProfile(dto.getProfile())
        .build();
  }

  public ConfigPropsInfoDto fromProto(ConfigPropsInfo info) {
    return ConfigPropsInfoDto.builder()
        .configId(fromProto(info.getConfigId()))
        .props(fromProto(info.getProps()))
        .build();
  }

  public ConfigPropsInfo toProto(ConfigPropsInfoDto dto) {
    return ConfigPropsInfo.newBuilder()
        .setConfigId(toProto(dto.getConfigId()))
        .setProps(toProto(dto.getProps()))
        .build();
  }
}
