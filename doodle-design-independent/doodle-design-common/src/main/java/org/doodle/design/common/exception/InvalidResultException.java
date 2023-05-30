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
package org.doodle.design.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.doodle.design.common.Code;
import org.doodle.design.common.Result;
import org.doodle.design.common.Status;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidResultException extends RuntimeException {
  int code;

  public InvalidResultException() {
    this.code = Code.BAD_VALUE;
  }

  public InvalidResultException(Status status) {
    super(status.getMessage());
    this.code = status.getCode();
  }

  public InvalidResultException(Result<?> r) {
    super(r.getMessage());
    this.code = r.getCode();
  }
}
