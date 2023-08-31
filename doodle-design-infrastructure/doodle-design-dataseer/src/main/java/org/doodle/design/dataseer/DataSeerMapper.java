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
package org.doodle.design.dataseer;

import org.doodle.design.common.ProtoMapper;

public abstract class DataSeerMapper implements ProtoMapper {

  public LogMessageInfo toProto(org.doodle.design.dataseer.model.info.LogMessageInfo info) {
    return LogMessageInfo.newBuilder()
        .setType(info.getType())
        .putAllTags(info.getTags())
        .setPayload(toProto(info.getPayload()))
        .build();
  }

  public org.doodle.design.dataseer.model.info.LogMessageInfo fromProto(LogMessageInfo info) {
    return org.doodle.design.dataseer.model.info.LogMessageInfo.builder()
        .type(info.getType())
        .tags(info.getTagsMap())
        .payload(fromProto(info.getPayload()))
        .build();
  }
}
