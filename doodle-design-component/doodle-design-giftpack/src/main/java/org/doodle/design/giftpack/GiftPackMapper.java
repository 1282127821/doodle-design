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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.doodle.design.common.ProtoMapper;
import org.springframework.util.CollectionUtils;

public abstract class GiftPackMapper implements ProtoMapper {

  public VisionInfo toProto(org.doodle.design.giftpack.model.info.VisionInfo info) {
    return VisionInfo.newBuilder()
        .setVisionId(info.getVisionId())
        .setDescription(info.getDescription())
        .build();
  }

  public org.doodle.design.giftpack.model.info.VisionInfo fromProto(VisionInfo proto) {
    return org.doodle.design.giftpack.model.info.VisionInfo.builder()
        .visionId(proto.getVisionId())
        .description(proto.getDescription())
        .build();
  }

  public VisionInfoList toVisionInfoList(
      List<org.doodle.design.giftpack.model.info.VisionInfo> infos) {
    VisionInfoList.Builder builder = VisionInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addVision);
    }
    return builder.build();
  }

  public List<org.doodle.design.giftpack.model.info.VisionInfo> fromProtoList(
      VisionInfoList proto) {
    return !CollectionUtils.isEmpty(proto.getVisionList())
        ? proto.getVisionList().stream().map(this::fromProto).toList()
        : Collections.emptyList();
  }

  public GiftInfo toProto(org.doodle.design.giftpack.model.info.GiftInfo info) {
    return GiftInfo.newBuilder()
        .setGiftId(info.getGiftId())
        .setContent(info.getContent())
        .setVision(toProto(info.getVision()))
        .build();
  }

  public org.doodle.design.giftpack.model.info.GiftInfo fromProto(GiftInfo proto) {
    return org.doodle.design.giftpack.model.info.GiftInfo.builder()
        .giftId(proto.getGiftId())
        .content(proto.getContent())
        .vision(fromProto(proto.getVision()))
        .build();
  }

  public GiftInfoList toGiftInfoList(List<org.doodle.design.giftpack.model.info.GiftInfo> infos) {
    GiftInfoList.Builder gift = GiftInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(gift::addGift);
    }
    return gift.build();
  }

  public List<org.doodle.design.giftpack.model.info.GiftInfo> fromProtoList(GiftInfoList proto) {
    return !CollectionUtils.isEmpty(proto.getGiftList())
        ? proto.getGiftList().stream().map(this::fromProto).toList()
        : Collections.emptyList();
  }

  public CodeInfo toProto(org.doodle.design.giftpack.model.info.CodeInfo info) {
    return CodeInfo.newBuilder()
        .setCodeId(info.getCodeId())
        .setPackCode(info.getPackCode())
        .setGift(this.toGiftInfoList(info.getGiftInfos()))
        .build();
  }

  public org.doodle.design.giftpack.model.info.CodeInfo fromProto(CodeInfo proto) {
    return org.doodle.design.giftpack.model.info.CodeInfo.builder()
        .codeId(proto.getCodeId())
        .packCode(proto.getPackCode())
        .giftInfos(fromProtoList(proto.getGift()))
        .build();
  }

  public CodeInfoList toCodeInfoList(List<org.doodle.design.giftpack.model.info.CodeInfo> infos) {
    CodeInfoList.Builder builder = CodeInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addCode);
    }
    return builder.build();
  }

  public List<org.doodle.design.giftpack.model.info.CodeInfo> fromProtoList(CodeInfoList proto) {
    return !CollectionUtils.isEmpty(proto.getCodeList())
        ? proto.getCodeList().stream().map(this::fromProto).toList()
        : Collections.emptyList();
  }

  public PackLifecycleInfo toProto(org.doodle.design.giftpack.model.info.PackLifecycleInfo info) {
    return PackLifecycleInfo.newBuilder()
        .setStart(toProto(info.getStart().atZone(ZoneId.systemDefault()).toInstant()))
        .setEnd(toProto(info.getEnd().atZone(ZoneId.systemDefault()).toInstant()))
        .build();
  }

  public org.doodle.design.giftpack.model.info.PackLifecycleInfo fromProto(
      PackLifecycleInfo proto) {
    return org.doodle.design.giftpack.model.info.PackLifecycleInfo.builder()
        .start(LocalDateTime.ofInstant(fromProto(proto.getStart()), ZoneId.systemDefault()))
        .end(LocalDateTime.ofInstant(fromProto(proto.getEnd()), ZoneId.systemDefault()))
        .build();
  }

  public PackOptionsInfo toProto(org.doodle.design.giftpack.model.info.PackOptionsInfo info) {
    return PackOptionsInfo.newBuilder().setEnable(info.isEnable()).build();
  }

  public org.doodle.design.giftpack.model.info.PackOptionsInfo fromProto(PackOptionsInfo proto) {
    return org.doodle.design.giftpack.model.info.PackOptionsInfo.builder()
        .enable(proto.getEnable())
        .build();
  }

  public PackConditionInfo toProto(org.doodle.design.giftpack.model.info.PackConditionInfo info) {
    return PackConditionInfo.newBuilder()
        .setMatchType(info.getMatchType())
        .putAllCondition(info.getCondition())
        .build();
  }

  public org.doodle.design.giftpack.model.info.PackConditionInfo fromProto(
      PackConditionInfo proto) {
    return org.doodle.design.giftpack.model.info.PackConditionInfo.builder()
        .matchType(proto.getMatchType())
        .condition(proto.getConditionMap())
        .build();
  }

  public UniversalPackDetailInfo toProto(
      org.doodle.design.giftpack.model.info.UniversalPackDetailInfo info) {
    return UniversalPackDetailInfo.newBuilder()
        .setQuantity(info.getQuantity())
        .setTimes(info.getTimes())
        .build();
  }

  public org.doodle.design.giftpack.model.info.UniversalPackDetailInfo fromProto(
      UniversalPackDetailInfo proto) {
    return org.doodle.design.giftpack.model.info.UniversalPackDetailInfo.builder()
        .quantity(proto.getQuantity())
        .times(proto.getTimes())
        .build();
  }

  public TemporaryPackDetailInfo toProto(
      org.doodle.design.giftpack.model.info.TemporaryPackDetailInfo info) {
    return TemporaryPackDetailInfo.newBuilder()
        .setQuantity(info.getQuantity())
        .setBatch(info.getBatch())
        .build();
  }

  public org.doodle.design.giftpack.model.info.TemporaryPackDetailInfo fromProto(
      TemporaryPackDetailInfo proto) {
    return org.doodle.design.giftpack.model.info.TemporaryPackDetailInfo.builder()
        .quantity(proto.getQuantity())
        .batch(proto.getBatch())
        .build();
  }

  public PackDetailInfo toProto(org.doodle.design.giftpack.model.info.PackDetailInfo info) {
    PackDetailInfo.Builder builder = PackDetailInfo.newBuilder();
    if (Objects.nonNull(info.getUniversal())) {
      builder.setUniversal(toProto(info.getUniversal()));
    } else if (Objects.nonNull(info.getTemporary())) {
      builder.setTemporary(toProto(info.getTemporary()));
    }
    return builder.build();
  }

  public org.doodle.design.giftpack.model.info.PackDetailInfo fromProto(PackDetailInfo proto) {
    org.doodle.design.giftpack.model.info.PackDetailInfo.PackDetailInfoBuilder builder =
        org.doodle.design.giftpack.model.info.PackDetailInfo.builder();
    if (proto.hasUniversal()) {
      builder.universal(fromProto(proto.getUniversal()));
    } else if (proto.hasTemporary()) {
      builder.temporary(fromProto(proto.getTemporary()));
    }
    return builder.build();
  }

  public PackInfo toProto(org.doodle.design.giftpack.model.info.PackInfo info) {
    return PackInfo.newBuilder()
        .setPackId(info.getPackId())
        .setCode(toProto(info.getCode()))
        .setLifecycle(toProto(info.getLifecycle()))
        .setCondition(toProto(info.getCondition()))
        .setOptions(toProto(info.getOptions()))
        .setDetail(toProto(info.getDetail()))
        .build();
  }

  public org.doodle.design.giftpack.model.info.PackInfo fromProto(PackInfo proto) {
    return org.doodle.design.giftpack.model.info.PackInfo.builder()
        .packId(proto.getPackId())
        .code(fromProto(proto.getCode()))
        .lifecycle(fromProto(proto.getLifecycle()))
        .condition(fromProto(proto.getCondition()))
        .options(fromProto(proto.getOptions()))
        .detail(fromProto(proto.getDetail()))
        .build();
  }

  public PackInfoList toPackInfoList(List<org.doodle.design.giftpack.model.info.PackInfo> infos) {
    PackInfoList.Builder builder = PackInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addPack);
    }
    return builder.build();
  }

  public List<org.doodle.design.giftpack.model.info.PackInfo> fromProtoList(PackInfoList proto) {
    return !CollectionUtils.isEmpty(proto.getPackList())
        ? proto.getPackList().stream().map(this::fromProto).toList()
        : Collections.emptyList();
  }

  public CodeUserInfo toProto(org.doodle.design.giftpack.model.info.CodeUserInfo info) {
    return CodeUserInfo.newBuilder().setUserId(info.getUserId()).putAllVars(info.getVars()).build();
  }

  public org.doodle.design.giftpack.model.info.CodeUserInfo fromProto(CodeUserInfo proto) {
    return org.doodle.design.giftpack.model.info.CodeUserInfo.builder()
        .userId(proto.getUserId())
        .vars(proto.getVarsMap())
        .build();
  }

  public GiftPackVisionQueryReply toVisionQueryReply(VisionInfo info) {
    return GiftPackVisionQueryReply.newBuilder().setPayload(info).build();
  }

  public GiftPackVisionQueryReply toVisionQueryError(GiftPackErrorCode errorCode) {
    return GiftPackVisionQueryReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackVisionPageReply toVisionPageReply(VisionInfoList infos) {
    return GiftPackVisionPageReply.newBuilder().setPayload(infos).build();
  }

  public GiftPackVisionPageReply toVisionPageError(GiftPackErrorCode errorCode) {
    return GiftPackVisionPageReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackGiftQueryReply toGiftQueryReply(GiftInfo info) {
    return GiftPackGiftQueryReply.newBuilder().setPayload(info).build();
  }

  public GiftPackGiftQueryReply toGiftQueryError(GiftPackErrorCode errorCode) {
    return GiftPackGiftQueryReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackGiftPageReply toGiftPageReply(GiftInfoList infos) {
    return GiftPackGiftPageReply.newBuilder().setPayload(infos).build();
  }

  public GiftPackGiftPageReply toGiftPageError(GiftPackErrorCode errorCode) {
    return GiftPackGiftPageReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackCodeQueryReply toCodeQueryReply(CodeInfo info) {
    return GiftPackCodeQueryReply.newBuilder().setPayload(info).build();
  }

  public GiftPackCodeQueryReply toCodeQueryError(GiftPackErrorCode errorCode) {
    return GiftPackCodeQueryReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackCodePageReply toCodePageReply(CodeInfoList infos) {
    return GiftPackCodePageReply.newBuilder().setPayload(infos).build();
  }

  public GiftPackCodePageReply toCodePageError(GiftPackErrorCode errorCode) {
    return GiftPackCodePageReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackCodeUseReply toCodeUseReply(CodeInfo info) {
    return GiftPackCodeUseReply.newBuilder().setPayload(info).build();
  }

  public GiftPackCodeUseReply toCodeUseError(GiftPackErrorCode errorCode) {
    return GiftPackCodeUseReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackPackQueryReply toPackQueryReply(PackInfo info) {
    return GiftPackPackQueryReply.newBuilder().setPayload(info).build();
  }

  public GiftPackPackQueryReply toPackQueryError(GiftPackErrorCode errorCode) {
    return GiftPackPackQueryReply.newBuilder().setError(errorCode).build();
  }

  public GiftPackPackPageReply toPackPageReply(PackInfoList infos) {
    return GiftPackPackPageReply.newBuilder().setPayload(infos).build();
  }

  public GiftPackPackPageReply toPackPageError(GiftPackErrorCode errorCode) {
    return GiftPackPackPageReply.newBuilder().setError(errorCode).build();
  }
}
