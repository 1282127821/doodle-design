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
package org.doodle.design.console;

import org.doodle.design.common.ProtoMapper;

public abstract class ConsoleMapper implements ProtoMapper {

  public ApplicationInfo toProto(org.doodle.design.console.model.info.ApplicationInfo info) {
    return ApplicationInfo.newBuilder()
        .setApplicationId(info.getApplicationId())
        .setTags(toProto(info.getTags()))
        .build();
  }

  public org.doodle.design.console.model.info.ApplicationInfo fromProto(ApplicationInfo proto) {
    return org.doodle.design.console.model.info.ApplicationInfo.builder()
        .applicationId(proto.getApplicationId())
        .tags(fromProto(proto.getTags()))
        .build();
  }

  public ConsoleApplicationCreateReply toApplicationCreateReply(ApplicationInfo info) {
    return ConsoleApplicationCreateReply.newBuilder().setPayload(info).build();
  }

  public ConsoleApplicationCreateReply toApplicationCreateError(ConsoleErrorCode errorCode) {
    return ConsoleApplicationCreateReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleApplicationQueryReply toApplicationQueryReply(ApplicationInfo info) {
    return ConsoleApplicationQueryReply.newBuilder().setPayload(info).build();
  }

  public ConsoleApplicationQueryReply toApplicationQueryError(ConsoleErrorCode errorCode) {
    return ConsoleApplicationQueryReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleApplicationUpdateReply toApplicationUpdateReply(ApplicationInfo info) {
    return ConsoleApplicationUpdateReply.newBuilder().setPayload(info).build();
  }

  public ConsoleApplicationUpdateReply toApplicationUpdateError(ConsoleErrorCode errorCode) {
    return ConsoleApplicationUpdateReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleApplicationPageReply toApplicationPageReply(ApplicationInfoList infos) {
    return ConsoleApplicationPageReply.newBuilder().setPayload(infos).build();
  }

  public ConsoleApplicationPageReply toApplicationPageError(ConsoleErrorCode errorCode) {
    return ConsoleApplicationPageReply.newBuilder().setError(errorCode).build();
  }
}
