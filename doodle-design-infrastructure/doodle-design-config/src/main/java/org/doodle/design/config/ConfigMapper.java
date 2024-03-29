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

public abstract class ConfigMapper implements ProtoMapper {

  public ConfigIdInfo toProto(org.doodle.design.config.model.info.ConfigIdInfo info) {
    return ConfigIdInfo.newBuilder()
        .setGroup(info.getGroup())
        .setType(info.getType())
        .setApplicationId(info.getApplicationId())
        .setProfile(info.getProfile())
        .build();
  }

  public org.doodle.design.config.model.info.ConfigIdInfo fromProto(ConfigIdInfo proto) {
    return org.doodle.design.config.model.info.ConfigIdInfo.builder()
        .group(proto.getGroup())
        .type(proto.getType())
        .applicationId(proto.getApplicationId())
        .profile(proto.getProfile())
        .build();
  }
}
