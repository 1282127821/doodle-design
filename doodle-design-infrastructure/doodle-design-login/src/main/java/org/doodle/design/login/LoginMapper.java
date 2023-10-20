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
import org.doodle.design.login.model.info.LoginAccountAuthTokenInfo;

public abstract class LoginMapper implements ProtoMapper {

  public LoginAccountAuthToken toProto(LoginAccountAuthTokenInfo info) {
    return LoginAccountAuthToken.newBuilder()
        .setAccountId(info.getAccountId())
        .setSignToken(info.getSignToken())
        .setTimestamp(info.getTimestamp())
        .build();
  }

  public LoginAccountAuthTokenInfo fromProto(LoginAccountAuthToken proto) {
    return LoginAccountAuthTokenInfo.builder()
        .accountId(proto.getAccountId())
        .signToken(proto.getSignToken())
        .timestamp(proto.getTimestamp())
        .build();
  }

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
}
