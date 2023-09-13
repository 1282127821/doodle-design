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
package org.doodle.design.pipeline;

import org.doodle.design.common.ProtoMapper;

public abstract class PipelineMapper implements ProtoMapper {

  public AgentInfo toProto(org.doodle.design.pipeline.model.info.AgentInfo info) {
    return AgentInfo.newBuilder().setAgentId(info.getAgentId()).build();
  }

  public org.doodle.design.pipeline.model.info.AgentInfo fromProto(AgentInfo proto) {
    return org.doodle.design.pipeline.model.info.AgentInfo.builder()
        .agentId(proto.getAgentId())
        .build();
  }
}
