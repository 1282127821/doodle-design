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

public abstract class ExcelMapper implements ProtoMapper {

  public SheetInfo toProto(org.doodle.design.excel.model.info.SheetInfo info) {
    return SheetInfo.newBuilder()
        .setXlsxName(info.getXlsxName())
        .setSheetName(info.getSheetName())
        .setConfig(toProto(info.getConfig()))
        .build();
  }

  public org.doodle.design.excel.model.info.SheetInfo fromProto(SheetInfo proto) {
    return org.doodle.design.excel.model.info.SheetInfo.builder()
        .xlsxName(proto.getXlsxName())
        .sheetName(proto.getSheetName())
        .config(fromProto(proto.getConfig()))
        .build();
  }

  public ExcelSheetQueryReply toProto(SheetInfo info) {
    return ExcelSheetQueryReply.newBuilder().setPayload(info).build();
  }
}
