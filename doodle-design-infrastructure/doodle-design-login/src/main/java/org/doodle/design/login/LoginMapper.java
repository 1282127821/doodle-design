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

import java.util.Objects;
import org.doodle.design.common.ProtoMapper;

public abstract class LoginMapper implements ProtoMapper {

  public LoginAccountInfo toProto(org.doodle.design.login.model.info.LoginAccountInfo info) {
    return LoginAccountInfo.newBuilder()
        .setAccountId(info.getAccountId())
        .setSdkType(info.getSdkType())
        .build();
  }

  public org.doodle.design.login.model.info.LoginAccountInfo fromProto(LoginAccountInfo proto) {
    return org.doodle.design.login.model.info.LoginAccountInfo.builder()
        .accountId(proto.getAccountId())
        .sdkType(proto.getSdkType())
        .build();
  }

  public LoginVirtualInfo toProto(org.doodle.design.login.model.info.LoginVirtualInfo info) {
    return LoginVirtualInfo.newBuilder()
        .setVirtualId(info.getVirtualId())
        .setAccount(toProto(info.getAccountInfo()))
        .build();
  }

  public org.doodle.design.login.model.info.LoginVirtualInfo fromProto(LoginVirtualInfo proto) {
    return org.doodle.design.login.model.info.LoginVirtualInfo.builder()
        .virtualId(proto.getVirtualId())
        .accountInfo(fromProto(proto.getAccount()))
        .build();
  }

  public LoginAccountPayloadInfo toProto(
      org.doodle.design.login.model.info.LoginAccountPayloadInfo info) {
    LoginAccountPayloadInfo.Builder builder = LoginAccountPayloadInfo.newBuilder();
    if (Objects.nonNull(info.getAccountInfo())) {
      builder.setAccount(toProto(info.getAccountInfo()));
    } else if (Objects.nonNull(info.getVirtualInfo())) {
      builder.setVirtual(toProto(info.getVirtualInfo()));
    }
    return builder.build();
  }

  public org.doodle.design.login.model.info.LoginAccountPayloadInfo fromProto(
      LoginAccountPayloadInfo proto) {
    org.doodle.design.login.model.info.LoginAccountPayloadInfo.LoginAccountPayloadInfoBuilder
        builder = org.doodle.design.login.model.info.LoginAccountPayloadInfo.builder();
    if (proto.hasAccount()) {
      builder.accountInfo(fromProto(proto.getAccount()));
    } else if (proto.hasVirtual()) {
      builder.virtualInfo(fromProto(proto.getVirtual()));
    }
    return builder.build();
  }

  public LoginAccountBindReply toAccountBindReply(String token) {
    return LoginAccountBindReply.newBuilder().setPayload(token).build();
  }

  public LoginAccountBindReply toAccountBindError(LoginErrorCode errorCode) {
    return LoginAccountBindReply.newBuilder().setError(errorCode).build();
  }

  public LoginAccountAuthReply toAccountAuthReply(LoginAccountPayloadInfo info) {
    return LoginAccountAuthReply.newBuilder().setPayload(info).build();
  }

  public LoginAccountAuthReply toAccountAuthError(LoginErrorCode errorCode) {
    return LoginAccountAuthReply.newBuilder().setError(errorCode).build();
  }
}
