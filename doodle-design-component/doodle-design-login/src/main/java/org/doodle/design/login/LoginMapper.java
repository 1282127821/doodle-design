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

import java.util.stream.Collectors;
import org.doodle.design.common.ProtoMapper;
import org.doodle.design.common.Result;
import org.doodle.design.common.util.ProtoUtils;
import org.doodle.design.login.model.dto.AccountAuthTokenInfoDto;
import org.doodle.design.login.model.dto.AccountInfoDto;
import org.doodle.design.login.model.dto.RoleInfoDto;

public abstract class LoginMapper implements ProtoMapper {

  public Result<org.doodle.design.login.model.payload.reply.AccountAuthReply> fromProto(
      AccountAuthReply reply) {
    switch (reply.getResultCase()) {
      case ERROR -> {
        return ProtoUtils.fromProto(reply.getError());
      }
      case REPLY -> {
        return Result.ok(
            org.doodle.design.login.model.payload.reply.AccountAuthReply.builder()
                .token(fromProto(reply.getReply()))
                .build());
      }
      default -> {
        return Result.bad();
      }
    }
  }

  public AccountAuthRequest toProto(
      org.doodle.design.login.model.payload.request.AccountAuthRequest request) {
    return AccountAuthRequest.newBuilder().setSdkBundle(toProto(request.getSdkBundle())).build();
  }

  public Result<org.doodle.design.login.model.payload.reply.RolePullReply> fromProto(
      RolePullReply reply) {
    switch (reply.getResultCase()) {
      case ERROR -> {
        return ProtoUtils.fromProto(reply.getError());
      }
      case REPLY -> {
        return Result.ok(
            org.doodle.design.login.model.payload.reply.RolePullReply.builder()
                .roleInfoList(
                    reply.getReply().getRoleInfoList().stream()
                        .map(this::fromProto)
                        .collect(Collectors.toList()))
                .build());
      }
      default -> {
        return Result.bad();
      }
    }
  }

  public RolePullRequest toProto(
      org.doodle.design.login.model.payload.request.RolePullRequest request) {
    return RolePullRequest.newBuilder().setAccountId(request.getAccountId()).build();
  }

  public AccountInfoDto fromProto(AccountInfo info) {
    return AccountInfoDto.builder()
        .accountId(info.getAccountId())
        .sdkBundle(fromProto(info.getSdkBundle()))
        .extraParams(fromProto(info.getExtraParams()))
        .build();
  }

  public AccountInfo toProto(AccountInfoDto dto) {
    return AccountInfo.newBuilder()
        .setAccountId(dto.getAccountId())
        .setSdkBundle(toProto(dto.getSdkBundle()))
        .setExtraParams(toProto(dto.getExtraParams()))
        .build();
  }

  public RoleInfoDto fromProto(RoleInfo info) {
    return RoleInfoDto.builder()
        .roleId(info.getRoleId())
        .sdkBundle(fromProto(info.getSdkBundle()))
        .roleName(info.getRoleName())
        .roleLevel(info.getRoleLevel())
        .extraParams(fromProto(info.getExtraParams()))
        .build();
  }

  public RoleInfo toProto(RoleInfoDto dto) {
    return RoleInfo.newBuilder()
        .setRoleId(dto.getRoleId())
        .setSdkBundle(toProto(dto.getSdkBundle()))
        .setRoleName(dto.getRoleName())
        .setRoleLevel(dto.getRoleLevel())
        .setExtraParams(toProto(dto.getExtraParams()))
        .build();
  }

  public AccountAuthTokenInfoDto fromProto(AccountAuthTokenInfo info) {
    return AccountAuthTokenInfoDto.builder()
        .token(info.getToken())
        .accountId(info.getAccountId())
        .timestamp(fromProto(info.getTimestamp()))
        .build();
  }

  public AccountAuthTokenInfo toProto(AccountAuthTokenInfoDto dto) {
    return AccountAuthTokenInfo.newBuilder()
        .setToken(dto.getToken())
        .setAccountId(dto.getAccountId())
        .setTimestamp(toProto(dto.getTimestamp()))
        .build();
  }
}
