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
package org.doodle.design.giftpack;

import org.doodle.design.common.Result;
import reactor.core.publisher.Mono;

public interface GiftPackHashCreateOps {

  @FunctionalInterface
  interface RSocket {
    String CREATE_MAPPING = "giftpack.hash.create";

    Mono<GiftPackHashCreateReply> create(GiftPackHashCreateRequest request);
  }

  @FunctionalInterface
  interface Servlet {
    String CREATE_MAPPING = "/giftpack/hash/create";

    Result<org.doodle.design.giftpack.model.payload.reply.GiftPackHashCreateReply> create(
        org.doodle.design.giftpack.model.payload.request.GiftPackHashCreateRequest request);
  }
}
