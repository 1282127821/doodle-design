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

import java.util.Collections;
import java.util.List;
import org.doodle.design.common.ProtoMapper;
import org.springframework.util.CollectionUtils;

public abstract class DataSeerMapper implements ProtoMapper {

  public ReportLog toProto(org.doodle.design.dataseer.model.info.ReportLog info) {
    return ReportLog.newBuilder().setLog(toProto(info.getLogInfo())).build();
  }

  public org.doodle.design.dataseer.model.info.ReportLog fromProto(ReportLog proto) {
    return org.doodle.design.dataseer.model.info.ReportLog.builder()
        .logInfo(fromProto(proto.getLog()))
        .build();
  }

  public ReportLogList toReportLogList(
      List<org.doodle.design.dataseer.model.info.ReportLog> infos) {
    ReportLogList.Builder builder = ReportLogList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addReportLog);
    }
    return builder.build();
  }

  public List<org.doodle.design.dataseer.model.info.ReportLog> fromProtoList(ReportLogList proto) {
    return !CollectionUtils.isEmpty(proto.getReportLogList())
        ? proto.getReportLogList().stream().map(this::fromProto).toList()
        : Collections.emptyList();
  }

  public DataSeerReportLogPageReply toReportLogPageReply(ReportLogList logs) {
    return DataSeerReportLogPageReply.newBuilder().setPayload(logs).build();
  }

  public org.doodle.design.dataseer.model.payload.reply.DataSeerReportLogPageReply
      toReportPageReply(List<org.doodle.design.dataseer.model.info.ReportLog> logs) {
    return org.doodle.design.dataseer.model.payload.reply.DataSeerReportLogPageReply.builder()
        .reportLogs(logs)
        .build();
  }

  public DataSeerReportLogPageReply toReportLogPageError(DataSeerErrorCode errorCode) {
    return DataSeerReportLogPageReply.newBuilder().setError(errorCode).build();
  }
}
