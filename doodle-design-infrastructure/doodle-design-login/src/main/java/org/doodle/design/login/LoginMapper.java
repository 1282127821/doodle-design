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

import org.doodle.design.common.ProtoMapper;

public abstract class LoginMapper implements ProtoMapper {

  public LoginRoleInfo toProto(org.doodle.design.login.model.info.LoginRoleInfo info) {
    return LoginRoleInfo.newBuilder()
        .setRoleId(info.getRoleId())
        .setAccountId(info.getAccountId())
        .setRoleName(info.getRoleName())
        .setRoleLevel(info.getRoleLevel())
        .setServerId(info.getServerId())
        .build();
  }

  public org.doodle.design.login.model.info.LoginRoleInfo fromProto(LoginRoleInfo info) {
    return org.doodle.design.login.model.info.LoginRoleInfo.builder()
        .roleId(info.getRoleId())
        .accountId(info.getAccountId())
        .roleName(info.getRoleName())
        .roleLevel(info.getRoleLevel())
        .serverId(info.getServerId())
        .build();
  }
}
