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
package org.doodle.design.security;

import org.doodle.design.common.ProtoMapper;
import org.doodle.design.common.Status;

public abstract class SecurityMapper implements ProtoMapper {

  public SecurityPullReply toError(Status status) {
    return SecurityPullReply.newBuilder().setError(status).build();
  }

  public SecurityPullReply toReply(UserDetailsInfo info) {
    return SecurityPullReply.newBuilder().setReply(info).build();
  }
}
