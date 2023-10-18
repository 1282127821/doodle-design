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
package org.doodle.design.role;

import java.util.Collections;
import java.util.List;
import org.doodle.design.common.ProtoMapper;
import org.springframework.util.CollectionUtils;

public abstract class RoleMapper implements ProtoMapper {

  public RoleProfileInfo toProto(org.doodle.design.role.model.info.RoleProfileInfo info) {
    return RoleProfileInfo.newBuilder()
        .setAccountId(info.getAccountId())
        .setRoleId(info.getRoleId())
        .setRoleName(info.getRoleName())
        .setRoleLevel(info.getRoleLevel())
        .setServerId(info.getServerId())
        .setServerName(info.getServerName())
        .setLastActiveTime(info.getLastActiveTime())
        .build();
  }

  public org.doodle.design.role.model.info.RoleProfileInfo fromProto(RoleProfileInfo proto) {
    return org.doodle.design.role.model.info.RoleProfileInfo.builder()
        .accountId(proto.getAccountId())
        .roleId(proto.getRoleId())
        .roleName(proto.getRoleName())
        .roleLevel(proto.getRoleLevel())
        .serverId(proto.getServerId())
        .serverName(proto.getServerName())
        .lastActiveTime(proto.getLastActiveTime())
        .build();
  }

  public RoleProfileInfoList toProfileProtoList(
      List<org.doodle.design.role.model.info.RoleProfileInfo> infos) {
    RoleProfileInfoList.Builder builder = RoleProfileInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addRoleProfile);
    }
    return builder.build();
  }

  public List<org.doodle.design.role.model.info.RoleProfileInfo> fromProtoList(
      RoleProfileInfoList proto) {
    return !CollectionUtils.isEmpty(proto.getRoleProfileList())
        ? proto.getRoleProfileList().stream().map(this::fromProto).toList()
        : Collections.emptyList();
  }

  public RoleProfilePullReply toProfilePullReply(RoleProfileInfoList infos) {
    return RoleProfilePullReply.newBuilder().setPayload(infos).build();
  }

  public RoleProfilePullReply toProfilePullError(RoleErrorCode errorCode) {
    return RoleProfilePullReply.newBuilder().setError(errorCode).build();
  }
}
