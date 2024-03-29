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
package org.doodle.design.giftpack;

import java.util.List;
import java.util.Objects;
import org.doodle.design.common.ProtoMapper;
import org.springframework.util.CollectionUtils;

public abstract class GiftPackMapper implements ProtoMapper {

  public GiftPackLifecycleInfo toProto(
      org.doodle.design.giftpack.model.info.GiftPackLifecycleInfo info) {
    return GiftPackLifecycleInfo.newBuilder()
        .setStart(toProto(info.getStart()))
        .setEnd(toProto(info.getEnd()))
        .build();
  }

  public org.doodle.design.giftpack.model.info.GiftPackLifecycleInfo fromProto(
      GiftPackLifecycleInfo proto) {
    return org.doodle.design.giftpack.model.info.GiftPackLifecycleInfo.builder()
        .start(fromProto(proto.getStart()))
        .end(fromProto(proto.getEnd()))
        .build();
  }

  public GiftPackContentInfo toProto(
      org.doodle.design.giftpack.model.info.GiftPackContentInfo info) {
    return GiftPackContentInfo.newBuilder()
        .setContentId(info.getContentId())
        .setContent(info.getContent())
        .build();
  }

  public org.doodle.design.giftpack.model.info.GiftPackContentInfo fromProto(
      GiftPackContentInfo proto) {
    return org.doodle.design.giftpack.model.info.GiftPackContentInfo.builder()
        .contentId(proto.getContentId())
        .content(proto.getContent())
        .build();
  }

  public GiftPackContentInfoList toContentInfoList(
      List<org.doodle.design.giftpack.model.info.GiftPackContentInfo> infos) {
    GiftPackContentInfoList.Builder builder = GiftPackContentInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addContent);
    }
    return builder.build();
  }

  public GiftPackGroupInfo toProto(org.doodle.design.giftpack.model.info.GiftPackGroupInfo info) {
    return GiftPackGroupInfo.newBuilder()
        .setGroupId(info.getGroupId())
        .setGroupCode(info.getGroupCode())
        .setContent(toProto(info.getContentInfo()))
        .setLifecycle(toProto(info.getLifecycleInfo()))
        .build();
  }

  public org.doodle.design.giftpack.model.info.GiftPackGroupInfo fromProto(
      GiftPackGroupInfo proto) {
    return org.doodle.design.giftpack.model.info.GiftPackGroupInfo.builder()
        .groupId(proto.getGroupId())
        .groupCode(proto.getGroupCode())
        .contentInfo(fromProto(proto.getContent()))
        .lifecycleInfo(fromProto(proto.getLifecycle()))
        .build();
  }

  public GiftPackGroupInfoList toGroupInfoList(
      List<org.doodle.design.giftpack.model.info.GiftPackGroupInfo> infos) {
    GiftPackGroupInfoList.Builder builder = GiftPackGroupInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addGroup);
    }
    return builder.build();
  }

  public GiftPackBatchInfo toProto(org.doodle.design.giftpack.model.info.GiftPackBatchInfo info) {
    return GiftPackBatchInfo.newBuilder()
        .setBatchId(info.getBatchId())
        .setBatchSize(info.getBatchSize())
        .setContent(toProto(info.getContentInfo()))
        .setLifecycle(toProto(info.getLifecycleInfo()))
        .build();
  }

  public org.doodle.design.giftpack.model.info.GiftPackBatchInfo fromProto(
      GiftPackBatchInfo proto) {
    return org.doodle.design.giftpack.model.info.GiftPackBatchInfo.builder()
        .batchId(proto.getBatchId())
        .batchSize(proto.getBatchSize())
        .contentInfo(fromProto(proto.getContent()))
        .lifecycleInfo(fromProto(proto.getLifecycle()))
        .build();
  }

  public GiftPackBatchInfoList toBatchInfoList(
      List<org.doodle.design.giftpack.model.info.GiftPackBatchInfo> infos) {
    GiftPackBatchInfoList.Builder builder = GiftPackBatchInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addBatch);
    }
    return builder.build();
  }

  public GiftPackSpecInfo toProto(org.doodle.design.giftpack.model.info.GiftPackSpecInfo info) {
    return GiftPackSpecInfo.newBuilder()
        .setSpecId(info.getSpecId())
        .setRoleId(info.getRoleId())
        .setContent(toProto(info.getContentInfo()))
        .setLifecycle(toProto(info.getLifecycleInfo()))
        .build();
  }

  public org.doodle.design.giftpack.model.info.GiftPackSpecInfo fromProto(GiftPackSpecInfo proto) {
    return org.doodle.design.giftpack.model.info.GiftPackSpecInfo.builder()
        .specId(proto.getSpecId())
        .roleId(proto.getRoleId())
        .contentInfo(fromProto(proto.getContent()))
        .lifecycleInfo(fromProto(proto.getLifecycle()))
        .build();
  }

  public GiftPackSpecInfoList toSpecInfoList(
      List<org.doodle.design.giftpack.model.info.GiftPackSpecInfo> infos) {
    GiftPackSpecInfoList.Builder builder = GiftPackSpecInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addSpec);
    }
    return builder.build();
  }

  public GiftPackInfo toProto(org.doodle.design.giftpack.model.info.GiftPackInfo info) {
    GiftPackInfo.Builder builder = GiftPackInfo.newBuilder();
    if (Objects.nonNull(info.getGroupInfo())) {
      builder.setGroup(toProto(info.getGroupInfo()));
    } else if (Objects.nonNull(info.getBatchInfo())) {
      builder.setBatch(toProto(info.getBatchInfo()));
    } else if (Objects.nonNull(info.getSpecInfo())) {
      builder.setSpec(toProto(info.getSpecInfo()));
    }
    return builder.build();
  }

  public org.doodle.design.giftpack.model.info.GiftPackInfo fromProto(GiftPackInfo proto) {
    org.doodle.design.giftpack.model.info.GiftPackInfo.GiftPackInfoBuilder builder =
        org.doodle.design.giftpack.model.info.GiftPackInfo.builder();
    if (proto.hasGroup()) {
      builder.groupInfo(fromProto(proto.getGroup()));
    } else if (proto.hasBatch()) {
      builder.batchInfo(fromProto(proto.getBatch()));
    } else if (proto.hasSpec()) {
      builder.specInfo(fromProto(proto.getSpec()));
    }
    return builder.build();
  }

  public GiftPackContentQueryReply toContentQueryReply(GiftPackContentInfo info) {
    return GiftPackContentQueryReply.newBuilder().setPayload(info).build();
  }

  public GiftPackContentQueryReply toContentQueryError(GiftPackErrorCode errorCode) {
    return GiftPackContentQueryReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackContentPageReply toContentPageReply(GiftPackContentInfoList infoList) {
    return GiftPackContentPageReply.newBuilder().setPayload(infoList).build();
  }

  public GiftPackContentPageReply toContentPageError(GiftPackErrorCode errorCode) {
    return GiftPackContentPageReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackGroupQueryReply toGroupQueryReply(GiftPackGroupInfo info) {
    return GiftPackGroupQueryReply.newBuilder().setPayload(info).build();
  }

  public GiftPackGroupQueryReply toGroupQueryError(GiftPackErrorCode errorCode) {
    return GiftPackGroupQueryReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackGroupPageReply toGroupPageReply(GiftPackGroupInfoList infoList) {
    return GiftPackGroupPageReply.newBuilder().setPayload(infoList).build();
  }

  public GiftPackGroupPageReply toGroupPageError(GiftPackErrorCode errorCode) {
    return GiftPackGroupPageReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackBatchQueryReply toBatchQueryReply(GiftPackBatchInfo info) {
    return GiftPackBatchQueryReply.newBuilder().setPayload(info).build();
  }

  public GiftPackBatchQueryReply toBatchQueryError(GiftPackErrorCode errorCode) {
    return GiftPackBatchQueryReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackBatchPageReply toBatchPageReply(GiftPackBatchInfoList infoList) {
    return GiftPackBatchPageReply.newBuilder().setPayload(infoList).build();
  }

  public GiftPackBatchPageReply toBatchPageError(GiftPackErrorCode errorCode) {
    return GiftPackBatchPageReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackSpecQueryReply toSpecQueryReply(GiftPackSpecInfo info) {
    return GiftPackSpecQueryReply.newBuilder().setPayload(info).build();
  }

  public GiftPackSpecQueryReply toSpecQueryError(GiftPackErrorCode errorCode) {
    return GiftPackSpecQueryReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackSpecPageReply toSpecPageReply(GiftPackSpecInfoList infoList) {
    return GiftPackSpecPageReply.newBuilder().setPayload(infoList).build();
  }

  public GiftPackSpecPageReply toSpecPageError(GiftPackErrorCode errorCode) {
    return GiftPackSpecPageReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackRoleUseReply toRoleUseReply(GiftPackInfo info) {
    return GiftPackRoleUseReply.newBuilder().setPayload(info).build();
  }

  public GiftPackRoleUseReply toRoleUseError(GiftPackErrorCode errorCode) {
    return GiftPackRoleUseReply.newBuilder().setError(errorCode).build();
  }
}
