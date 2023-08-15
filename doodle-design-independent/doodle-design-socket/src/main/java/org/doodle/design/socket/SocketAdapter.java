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
package org.doodle.design.socket;

import io.rsocket.Payload;
import reactor.core.publisher.Mono;

public class SocketAdapter {

  private static final Mono<Void> UNSUPPORTED_ONEWAY =
      Mono.error(new UnsupportedInteractionException("ONEWAY"));

  static Mono<Void> oneway(Payload payload) {
    payload.release();
    return SocketAdapter.UNSUPPORTED_ONEWAY;
  }

  private static class UnsupportedInteractionException extends RuntimeException {

    UnsupportedInteractionException(String interactionName) {
      super(interactionName + "未实现", null, false, false);
    }
  }
}
