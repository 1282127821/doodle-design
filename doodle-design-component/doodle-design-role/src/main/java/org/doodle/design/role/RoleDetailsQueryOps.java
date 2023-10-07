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
package org.doodle.design.role;

import org.doodle.design.common.Result;
import reactor.core.publisher.Mono;

public interface RoleDetailsQueryOps {

  @FunctionalInterface
  interface RSocket {
    String QUERY_MAPPING = "role.details.query";

    Mono<RoleDetailsQueryReply> query(RoleDetailsQueryRequest request);
  }

  @FunctionalInterface
  interface Servlet {
    String QUERY_MAPPING = "/role/details/query";

    Result<org.doodle.design.role.model.payload.reply.RoleDetailsQueryReply> query(
        org.doodle.design.role.model.payload.request.RoleDetailsQueryRequest request);
  }
}
