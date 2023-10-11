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
package org.doodle.design.notice.model;

import org.doodle.design.common.Result;
import org.doodle.design.notice.NoticeMenuPageReply;
import org.doodle.design.notice.NoticeMenuPageRequest;
import reactor.core.publisher.Mono;

public interface NoticeMenuPageOps {

  @FunctionalInterface
  interface RSocket {
    String PAGE_MAPPING = "notice.menu.page";

    Mono<NoticeMenuPageReply> page(NoticeMenuPageRequest request);
  }

  @FunctionalInterface
  interface Servlet {
    String PAGE_MAPPING = "/notice/menu/page";

    Result<org.doodle.design.notice.model.payload.reply.NoticeMenuPageReply> page(
        org.doodle.design.notice.model.payload.request.NoticeMenuPageRequest request);
  }
}
