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
package org.doodle.design.excel;

import org.doodle.design.common.ProtoMapper;
import org.doodle.design.common.Result;
import org.doodle.design.common.util.ProtoUtils;
import org.doodle.design.excel.model.dto.ExcelIdInfoDto;
import org.doodle.design.excel.model.dto.ExcelPropsInfoDto;

public abstract class ExcelMapper implements ProtoMapper {

  public Result<org.doodle.design.excel.model.payload.reply.ExcelPullReply> fromProto(
      ExcelPullReply reply) {
    switch (reply.getResultCase()) {
      case ERROR -> {
        return ProtoUtils.fromProto(reply.getError());
      }
      case REPLY -> {
        return Result.ok(
            org.doodle.design.excel.model.payload.reply.ExcelPullReply.builder()
                .excelProps(fromProto(reply.getReply()))
                .build());
      }
      default -> {
        return Result.bad();
      }
    }
  }

  public ExcelPullRequest toProto(
      org.doodle.design.excel.model.payload.request.ExcelPullRequest request) {
    return ExcelPullRequest.newBuilder().setExcelId(toProto(request.getExcelId())).build();
  }

  public ExcelPropsInfoDto fromProto(ExcelPropsInfo info) {
    return ExcelPropsInfoDto.builder()
        .excelId(fromProto(info.getExcelId()))
        .excelProps(fromProto(info.getExcelProps()))
        .build();
  }

  public ExcelPropsInfo toProto(ExcelPropsInfoDto dto) {
    return ExcelPropsInfo.newBuilder()
        .setExcelId(toProto(dto.getExcelId()))
        .setExcelProps(toProto(dto.getExcelProps()))
        .build();
  }

  public ExcelIdInfoDto fromProto(ExcelIdInfo info) {
    return ExcelIdInfoDto.builder().xlsx(info.getXlsx()).sheet(info.getSheet()).build();
  }

  public ExcelIdInfo toProto(ExcelIdInfoDto dto) {
    return ExcelIdInfo.newBuilder().setSheet(dto.getSheet()).setXlsx(dto.getXlsx()).build();
  }
}
