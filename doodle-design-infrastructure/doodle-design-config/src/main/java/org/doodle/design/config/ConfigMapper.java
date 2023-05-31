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
import org.doodle.design.common.Result;
import org.doodle.design.common.Status;
import org.doodle.design.common.util.ProtoUtils;
import org.doodle.design.config.model.dto.ConfigIdInfoDto;
import org.doodle.design.config.model.dto.ConfigPropsInfoDto;

public abstract class ConfigMapper implements ProtoMapper {

  public ConfigPullRequest toProto(
      org.doodle.design.config.model.payload.request.ConfigPullRequest request) {
    return ConfigPullRequest.newBuilder().setConfigId(toProto(request.getConfigId())).build();
  }

  public Result<org.doodle.design.config.model.payload.reply.ConfigPullReply> fromProto(
      ConfigPullReply reply) {
    switch (reply.getResultCase()) {
      case ERROR -> {
        return ProtoUtils.fromProto(reply.getError());
      }
      case CONFIG_PROPS -> {
        return Result.ok(
            org.doodle.design.config.model.payload.reply.ConfigPullReply.builder()
                .configProps(fromProto(reply.getConfigProps()))
                .build());
      }
      default -> {
        return Result.bad();
      }
    }
  }

  public ConfigPullReply toError(Status status) {
    return ConfigPullReply.newBuilder().setError(status).build();
  }

  public ConfigPullReply toReply(ConfigPropsInfo info) {
    return ConfigPullReply.newBuilder().setConfigProps(info).build();
  }

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
