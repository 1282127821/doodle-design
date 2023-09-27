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
package org.doodle.design.common;

import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.Value;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.doodle.design.common.model.SdkBundle;
import org.doodle.design.common.util.ProtoUtils;

public interface ProtoMapper {

  default LogInfo toProto(org.doodle.design.common.model.LogInfo info) {
    return LogInfo.newBuilder()
        .setLogContext(toProto(info.getContextInfo()))
        .setLogMessage(toProto(info.getMessageInfo()))
        .build();
  }

  default org.doodle.design.common.model.LogInfo fromProto(LogInfo proto) {
    return org.doodle.design.common.model.LogInfo.builder()
        .contextInfo(fromProto(proto.getLogContext()))
        .messageInfo(fromProto(proto.getLogMessage()))
        .build();
  }

  default LogContextInfo toProto(org.doodle.design.common.model.LogContextInfo info) {
    return LogContextInfo.newBuilder()
        .setContext(toProto(info.getContext()))
        .setNestedContext(toProto(info.getNestedContext()))
        .setApplication(toProto(info.getApplication()))
        .build();
  }

  default org.doodle.design.common.model.LogContextInfo fromProto(LogContextInfo proto) {
    return org.doodle.design.common.model.LogContextInfo.builder()
        .context(fromProto(proto.getContext()))
        .nestedContext(fromProto(proto.getNestedContext()))
        .application(fromProto(proto.getApplication()))
        .build();
  }

  default LogMessageInfo toProto(org.doodle.design.common.model.LogMessageInfo info) {
    return LogMessageInfo.newBuilder()
        .setLevel(info.getLevel())
        .setFileName(info.getFileName())
        .setMethodName(info.getMethodName())
        .setLineNumber(info.getLineNumber())
        .setContent(info.getContent())
        .build();
  }

  default org.doodle.design.common.model.LogMessageInfo fromProto(LogMessageInfo proto) {
    return org.doodle.design.common.model.LogMessageInfo.builder()
        .level(proto.getLevel())
        .fileName(proto.getFileName())
        .methodName(proto.getMethodName())
        .lineNumber(proto.getLineNumber())
        .content(proto.getContent())
        .build();
  }

  default Pair toProto(Map.Entry<String, String> entry) {
    return Pair.newBuilder().setKey(entry.getKey()).setValue(entry.getValue()).build();
  }

  default Map.Entry<String, String> fromProto(Pair pair) {
    return Map.entry(pair.getKey(), pair.getValue());
  }

  default org.doodle.design.common.model.PageRequest fromProto(PageRequest request) {
    return org.doodle.design.common.model.PageRequest.builder()
        .pageNumber(request.getPageNumber())
        .pageSize(request.getPageSize())
        .build();
  }

  default PageRequest toProto(org.doodle.design.common.model.PageRequest request) {
    return PageRequest.newBuilder()
        .setPageNumber(request.getPageNumber())
        .setPageSize(request.getPageSize())
        .build();
  }

  default SdkBundle fromProto(SdkBundleInfo info) {
    return SdkBundle.builder()
        .sdkType(info.getSdkType())
        .sdkBinding(info.getSdkBinding())
        .sdkGameId(info.getSdkGameId())
        .sdkChannelId(info.getSdkChannelId())
        .build();
  }

  default SdkBundleInfo toProto(SdkBundle bundle) {
    return SdkBundleInfo.newBuilder()
        .setSdkType(bundle.getSdkType())
        .setSdkBinding(bundle.getSdkBinding())
        .setSdkGameId(bundle.getSdkGameId())
        .setSdkChannelId(bundle.getSdkChannelId())
        .setSdkExtraParams(toProto(bundle.getSdkExtraParams()))
        .build();
  }

  default Instant fromProto(Timestamp any) {
    return ProtoUtils.fromProto(any);
  }

  default Timestamp toProto(Instant instant) {
    return ProtoUtils.toProto(instant);
  }

  default Object fromProto(Value any) {
    return ProtoUtils.fromProto(any);
  }

  default Map<String, Object> fromProto(Struct any) {
    return ProtoUtils.fromProto(any);
  }

  default List<Object> fromProto(ListValue any) {
    return ProtoUtils.fromProto(any);
  }

  default Value toProto(Object val) {
    return ProtoUtils.toProto(val);
  }

  default Struct toProto(Map<String, Object> val) {
    return ProtoUtils.toProto(val);
  }

  default ListValue toProto(List<Object> val) {
    return ProtoUtils.toProto(val);
  }
}
