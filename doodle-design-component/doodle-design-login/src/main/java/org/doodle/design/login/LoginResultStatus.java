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

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum LoginResultStatus {
  ACCOUNT_NOT_FOUND(-2, "找不到对应的账号"),
  TOKEN_AUTH_FAILED(-3, "token检验失败"),
  SDK_NOT_FOUND(-4, "找不到对应的SDK"),
  SDK_PAYLOAD_INVALID(-5, "SDK参数非法"),
  RESERVED(-100, "找不到对应错误码");

  final int status;
  final String message;

  LoginResultStatus(int status, String message) {
    this.status = status;
    this.message = message;
  }

  public static LoginResultStatus valueOf(int status) {
    return Arrays.stream(values()).filter(v -> v.status == status).findFirst().orElse(RESERVED);
  }

  public static String message(int status) {
    return Arrays.stream(values())
        .filter(v -> v.status == status)
        .findFirst()
        .map(LoginResultStatus::getMessage)
        .orElseGet(RESERVED::getMessage);
  }
}
