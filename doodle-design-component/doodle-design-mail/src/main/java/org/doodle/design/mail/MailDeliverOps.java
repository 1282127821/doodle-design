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
package org.doodle.design.mail;

import org.doodle.design.common.Result;
import reactor.core.publisher.Mono;

public interface MailDeliverOps {
  @FunctionalInterface
  interface RSocket {
    String DELIVER_MAPPING = "mail.deliver";

    Mono<MailErrorCode> deliver(MailDeliverRequest request);
  }

  @FunctionalInterface
  interface Servlet {
    String DELIVER_MAPPING = "/mail/deliver";

    Result<MailErrorCode> deliver(
        org.doodle.design.mail.model.payload.request.MailDeliverRequest request);
  }
}
