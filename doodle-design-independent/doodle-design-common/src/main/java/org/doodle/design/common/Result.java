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
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public final class Result<T> {
  public static final int OK = 0;
  public static final int BAD = -1;

  private int status;
  private T body;

  public static Builder status(int status) {
    return new DefaultBuilder(status);
  }

  public static Builder ok() {
    return status(OK);
  }

  public static <T> Result<T> ok(@Nullable T body) {
    return ok().body(body);
  }

  public static <T> Result<T> bad() {
    return status(BAD).body(null);
  }

  public static <T> Result<T> bad(int status) {
    Assert.isTrue(status != OK, "bad 错误状态码不能和 OK 相同");
    return status(status).body(null);
  }

  public static <T> Result<T> of(@NonNull Optional<T> body) {
    Assert.notNull(body, "body 不能为空");
    return body.map(Result::ok).orElseGet(Result::bad);
  }

  @FunctionalInterface
  public interface Builder {
    <T> Result<T> body(T body);
  }

  @RequiredArgsConstructor
  private static class DefaultBuilder implements Builder {
    private final int status;

    @Override
    public <T> Result<T> body(T body) {
      return new Result<>(this.status, body);
    }
  }
}
