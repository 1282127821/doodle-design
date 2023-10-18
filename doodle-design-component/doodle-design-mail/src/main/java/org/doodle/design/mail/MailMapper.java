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

import java.util.Objects;
import org.doodle.design.common.ProtoMapper;
import org.doodle.design.mail.model.info.MailDeliverRSocketRoute;
import org.doodle.design.mail.model.info.MailDeliverServletRoute;

public abstract class MailMapper implements ProtoMapper {

  public MailDeliverRoute.RSocketDeliverRoute toProto(MailDeliverRSocketRoute route) {
    return MailDeliverRoute.RSocketDeliverRoute.newBuilder().putAllTags(route.getTags()).build();
  }

  public MailDeliverRSocketRoute fromProto(MailDeliverRoute.RSocketDeliverRoute route) {
    return MailDeliverRSocketRoute.builder().tags(route.getTagsMap()).build();
  }

  public MailDeliverRoute.ServletDeliverRoute toProto(MailDeliverServletRoute route) {
    return MailDeliverRoute.ServletDeliverRoute.newBuilder().setUri(route.getUri()).build();
  }

  public MailDeliverServletRoute fromProto(MailDeliverRoute.ServletDeliverRoute route) {
    return MailDeliverServletRoute.builder().uri(route.getUri()).build();
  }

  public MailDeliverRoute toProto(org.doodle.design.mail.model.info.MailDeliverRoute route) {
    MailDeliverRoute.Builder builder = MailDeliverRoute.newBuilder();
    if (Objects.nonNull(route.getRsocket())) {
      builder.setRsocket(toProto(route.getRsocket()));
    } else if (Objects.nonNull(route.getServlet())) {
      builder.setServlet(toProto(route.getServlet()));
    }
    return builder.build();
  }

  public org.doodle.design.mail.model.info.MailDeliverRoute fromProto(MailDeliverRoute route) {
    org.doodle.design.mail.model.info.MailDeliverRoute.MailDeliverRouteBuilder builder =
        org.doodle.design.mail.model.info.MailDeliverRoute.builder();
    if (route.hasRsocket()) {
      builder.rsocket(fromProto(route.getRsocket()));
    } else if (route.hasServlet()) {
      builder.servlet(fromProto(route.getServlet()));
    }
    return builder.build();
  }

  public MailDeliverReply toDeliverReply(MailErrorCode errorCode) {
    return MailDeliverReply.newBuilder().setErrorCode(errorCode).build();
  }
}
