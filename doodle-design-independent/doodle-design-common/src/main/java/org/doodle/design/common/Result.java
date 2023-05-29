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
package org.doodle.design.common;

import java.util.Optional;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public final class Result<T> {
  public static final int OK = Code.OK_VALUE;
  public static final int BAD = Code.BAD_VALUE;

  int code;
  String message;
  T body;

  public static Builder code(int code, String message) {
    return new DefaultBuilder(code, message);
  }

  public static Builder code(int code) {
    return code(code, null);
  }

  public static <T> Result<T> ok(@Nullable T body) {
    return ok().body(body);
  }

  public static Builder ok() {
    return code(OK);
  }

  public static <T1, T2> Result<T1> bad(Result<T2> r) {
    return bad(r.getCode(), r.getMessage());
  }

  public static <T> Result<T> bad() {
    return bad(BAD, null);
  }

  public static <T> Result<T> bad(int code) {
    return bad(code, null);
  }

  public static <T> Result<T> bad(int code, String message) {
    Assert.isTrue(code != OK, "bad bad 错误状态码不能和 OK 相同");
    return code(code, message).body(null);
  }

  public static <T> Result<T> of(@NonNull Optional<T> body) {
    Assert.notNull(body, "body 不能为空");
    return body.map(Result::ok).orElseGet(Result::bad);
  }

  @FunctionalInterface
  public interface Builder {
    <T> Result<T> body(T body);
  }

  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  @RequiredArgsConstructor
  static class DefaultBuilder implements Builder {
    int code;
    String message;

    @Override
    public <T> Result<T> body(T body) {
      return new Result<>(this.code, this.message, body);
    }
  }
}
