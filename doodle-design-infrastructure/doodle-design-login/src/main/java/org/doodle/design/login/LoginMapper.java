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

import java.util.Collections;
import java.util.List;
import org.doodle.design.common.ProtoMapper;
import org.springframework.util.CollectionUtils;

public abstract class LoginMapper implements ProtoMapper {

  public LoginRoleInfo toProto(org.doodle.design.login.model.info.LoginRoleInfo info) {
    return LoginRoleInfo.newBuilder()
        .setRoleId(info.getRoleId())
        .setAccountId(info.getAccountId())
        .setRoleName(info.getRoleName())
        .setRoleLevel(info.getRoleLevel())
        .setServerId(info.getServerId())
        .setTimestamp(info.getTimestamp())
        .build();
  }

  public org.doodle.design.login.model.info.LoginRoleInfo fromProto(LoginRoleInfo info) {
    return org.doodle.design.login.model.info.LoginRoleInfo.builder()
        .roleId(info.getRoleId())
        .accountId(info.getAccountId())
        .roleName(info.getRoleName())
        .roleLevel(info.getRoleLevel())
        .serverId(info.getServerId())
        .timestamp(info.getTimestamp())
        .build();
  }

  public LoginRoleInfoList toRoleInfoList(
      List<org.doodle.design.login.model.info.LoginRoleInfo> infos) {
    LoginRoleInfoList.Builder builder = LoginRoleInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addRoleInfo);
    }
    return builder.build();
  }

  public List<org.doodle.design.login.model.info.LoginRoleInfo> fromProtoList(
      LoginRoleInfoList proto) {
    return !CollectionUtils.isEmpty(proto.getRoleInfoList())
        ? proto.getRoleInfoList().stream().map(this::fromProto).toList()
        : Collections.emptyList();
  }

  public LoginAccountBindReply toProto(LoginAccountAuthToken token) {
    return LoginAccountBindReply.newBuilder().setPayload(token).build();
  }

  public LoginAccountBindReply toAccountBindError(LoginErrorCode errorCode) {
    return LoginAccountBindReply.newBuilder().setError(errorCode).build();
  }

  public LoginAccountAuthReply toProto(LoginAccountInfo info) {
    return LoginAccountAuthReply.newBuilder().setPayload(info).build();
  }

  public LoginAccountAuthReply toAccountAuthError(LoginErrorCode errorCode) {
    return LoginAccountAuthReply.newBuilder().setError(errorCode).build();
  }

  public LoginRolePullReply toProto(LoginRoleInfoList info) {
    return LoginRolePullReply.newBuilder().setPayload(info).build();
  }

  public LoginRolePullReply toRolePullError(LoginErrorCode errorCode) {
    return LoginRolePullReply.newBuilder().setError(errorCode).build();
  }
}
