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
package org.doodle.design.idle.console;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.doodle.design.common.ProtoMapper;
import org.springframework.util.CollectionUtils;

public abstract class ConsoleMapper implements ProtoMapper {

  public ConsoleArchiveInfo toProto(
      org.doodle.design.idle.console.model.info.ConsoleArchiveInfo info) {
    return ConsoleArchiveInfo.newBuilder()
        .setUniqueId(info.getUniqueId())
        .setCategorySeqId(info.getCategorySeqId())
        .setType(info.getType())
        .setCategory(info.getCategory())
        .build();
  }

  public org.doodle.design.idle.console.model.info.ConsoleArchiveInfo fromProto(
      ConsoleArchiveInfo proto) {
    return org.doodle.design.idle.console.model.info.ConsoleArchiveInfo.builder()
        .uniqueId(proto.getUniqueId())
        .categorySeqId(proto.getCategorySeqId())
        .type(proto.getType())
        .category(proto.getCategory())
        .build();
  }

  public ConsoleCloudInfo toProto(org.doodle.design.idle.console.model.info.ConsoleCloudInfo info) {
    return ConsoleCloudInfo.newBuilder().setDescription(info.getDescription()).build();
  }

  public org.doodle.design.idle.console.model.info.ConsoleCloudInfo fromProto(
      ConsoleCloudInfo proto) {
    return org.doodle.design.idle.console.model.info.ConsoleCloudInfo.builder()
        .description(proto.getDescription())
        .build();
  }

  public ConsoleEcsIpInfo toProto(org.doodle.design.idle.console.model.info.ConsoleEcsIpInfo info) {
    ConsoleEcsIpInfo.Builder builder = ConsoleEcsIpInfo.newBuilder();
    if (!CollectionUtils.isEmpty(info.getWlan())) {
      builder.addAllWlan(info.getWlan());
    } else if (!CollectionUtils.isEmpty(info.getLan())) {
      builder.addAllLan(info.getLan());
    }
    return builder.build();
  }

  public org.doodle.design.idle.console.model.info.ConsoleEcsIpInfo fromProto(
      ConsoleEcsIpInfo proto) {
    org.doodle.design.idle.console.model.info.ConsoleEcsIpInfo.ConsoleEcsIpInfoBuilder builder =
        org.doodle.design.idle.console.model.info.ConsoleEcsIpInfo.builder();
    if (!CollectionUtils.isEmpty(proto.getWlanList())) {
      builder.wlan(proto.getWlanList());
    } else if (!CollectionUtils.isEmpty(proto.getLanList())) {
      builder.lan(proto.getLanList());
    }
    return builder.build();
  }

  public ConsoleEcsPortInfo toProto(
      org.doodle.design.idle.console.model.info.ConsoleEcsPortInfo info) {
    ConsoleEcsPortInfo.Builder builder =
        ConsoleEcsPortInfo.newBuilder().setAvailable(toIntProto(info.getAvailable()));
    if (!CollectionUtils.isEmpty(info.getAssignerMap())) {
      builder.putAllAssigner(info.getAssignerMap());
    }
    if (!CollectionUtils.isEmpty(info.getExposed())) {
      builder.addAllExposed(info.getExposed());
    }
    return builder.build();
  }

  public org.doodle.design.idle.console.model.info.ConsoleEcsPortInfo fromProto(
      ConsoleEcsPortInfo proto) {
    org.doodle.design.idle.console.model.info.ConsoleEcsPortInfo.ConsoleEcsPortInfoBuilder builder =
        org.doodle.design.idle.console.model.info.ConsoleEcsPortInfo.builder()
            .available(fromIntProto(proto.getAvailable()));
    if (!CollectionUtils.isEmpty(proto.getAssignerMap())) {
      builder.assignerMap(proto.getAssignerMap());
    }
    if (!CollectionUtils.isEmpty(proto.getExposedList())) {
      builder.exposed(new HashSet<>(proto.getExposedList()));
    }
    return builder.build();
  }

  public ConsoleEcsSshInfo toProto(
      org.doodle.design.idle.console.model.info.ConsoleEcsSshInfo info) {
    return ConsoleEcsSshInfo.newBuilder()
        .setUsername(info.getUsername())
        .setPassword(info.getPassword())
        .setPort(info.getPort())
        .build();
  }

  public org.doodle.design.idle.console.model.info.ConsoleEcsSshInfo fromProto(
      ConsoleEcsSshInfo proto) {
    return org.doodle.design.idle.console.model.info.ConsoleEcsSshInfo.builder()
        .username(proto.getUsername())
        .password(proto.getPassword())
        .port(proto.getPort())
        .build();
  }

  public ConsoleEcsInfo toProto(org.doodle.design.idle.console.model.info.ConsoleEcsInfo info) {
    return ConsoleEcsInfo.newBuilder()
        .setArchive(toProto(info.getArchiveInfo()))
        .setIp(toProto(info.getIpInfo()))
        .setPort(toProto(info.getPortInfo()))
        .setSsh(toProto(info.getSshInfo()))
        .build();
  }

  public org.doodle.design.idle.console.model.info.ConsoleEcsInfo fromProto(ConsoleEcsInfo proto) {
    return org.doodle.design.idle.console.model.info.ConsoleEcsInfo.builder()
        .archiveInfo(fromProto(proto.getArchive()))
        .ipInfo(fromProto(proto.getIp()))
        .portInfo(fromProto(proto.getPort()))
        .sshInfo(fromProto(proto.getSsh()))
        .build();
  }

  public ConsoleEcsInfoList toEcsInfoList(
      List<org.doodle.design.idle.console.model.info.ConsoleEcsInfo> infos) {
    ConsoleEcsInfoList.Builder builder = ConsoleEcsInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addEcs);
    }
    return builder.build();
  }

  public ConsoleHostInfo toProto(org.doodle.design.idle.console.model.info.ConsoleHostInfo info) {
    ConsoleHostInfo.Builder builder = ConsoleHostInfo.newBuilder();
    if (Objects.nonNull(info.getEcsInfo())) {
      builder.setEcs(toProto(info.getEcsInfo()));
    } else if (Objects.nonNull(info.getCloudInfo())) {
      builder.setCloud(toProto(info.getCloudInfo()));
    }
    return builder.build();
  }

  public org.doodle.design.idle.console.model.info.ConsoleHostInfo fromProto(
      ConsoleHostInfo proto) {
    org.doodle.design.idle.console.model.info.ConsoleHostInfo.ConsoleHostInfoBuilder builder =
        org.doodle.design.idle.console.model.info.ConsoleHostInfo.builder();
    if (proto.hasEcs()) {
      builder.ecsInfo(fromProto(proto.getEcs()));
    } else if (proto.hasCloud()) {
      builder.cloudInfo(fromProto(proto.getCloud()));
    }
    return builder.build();
  }

  public ConsoleComponentInfo toProto(
      org.doodle.design.idle.console.model.info.ConsoleComponentInfo info) {
    return ConsoleComponentInfo.newBuilder()
        .setArchive(toProto(info.getArchiveInfo()))
        .setHost(toProto(info.getHostInfo()))
        .build();
  }

  public org.doodle.design.idle.console.model.info.ConsoleComponentInfo fromProto(
      ConsoleComponentInfo proto) {
    return org.doodle.design.idle.console.model.info.ConsoleComponentInfo.builder()
        .archiveInfo(fromProto(proto.getArchive()))
        .hostInfo(fromProto(proto.getHost()))
        .build();
  }

  public ConsoleComponentInfoList toComponentInfoList(
      List<org.doodle.design.idle.console.model.info.ConsoleComponentInfo> infos) {
    ConsoleComponentInfoList.Builder builder = ConsoleComponentInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addComponent);
    }
    return builder.build();
  }

  public ConsoleCrossInfo toProto(org.doodle.design.idle.console.model.info.ConsoleCrossInfo info) {
    return ConsoleCrossInfo.newBuilder()
        .setArchive(toProto(info.getArchiveInfo()))
        .setHost(toProto(info.getHostInfo()))
        .build();
  }

  public org.doodle.design.idle.console.model.info.ConsoleCrossInfo fromProto(
      ConsoleCrossInfo proto) {
    return org.doodle.design.idle.console.model.info.ConsoleCrossInfo.builder()
        .archiveInfo(fromProto(proto.getArchive()))
        .hostInfo(fromProto(proto.getHost()))
        .build();
  }

  public ConsoleCrossInfoList toCrossInfoList(
      List<org.doodle.design.idle.console.model.info.ConsoleCrossInfo> infos) {
    ConsoleCrossInfoList.Builder builder = ConsoleCrossInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addCross);
    }
    return builder.build();
  }

  public ConsoleMongodbInfo toProto(
      org.doodle.design.idle.console.model.info.ConsoleMongodbInfo info) {
    return ConsoleMongodbInfo.newBuilder().setUrl(info.getUrl()).build();
  }

  public org.doodle.design.idle.console.model.info.ConsoleMongodbInfo fromProto(
      ConsoleMongodbInfo proto) {
    return org.doodle.design.idle.console.model.info.ConsoleMongodbInfo.builder()
        .url(proto.getUrl())
        .build();
  }

  public ConsoleDbInfo toProto(org.doodle.design.idle.console.model.info.ConsoleDbInfo info) {
    ConsoleDbInfo.Builder builder =
        ConsoleDbInfo.newBuilder()
            .setArchive(toProto(info.getArchiveInfo()))
            .setHost(toProto(info.getHostInfo()));
    if (Objects.nonNull(info.getMongodbInfo())) {
      builder.setMongodb(toProto(info.getMongodbInfo()));
    }
    return builder.build();
  }

  public org.doodle.design.idle.console.model.info.ConsoleDbInfo fromProto(ConsoleDbInfo proto) {
    org.doodle.design.idle.console.model.info.ConsoleDbInfo.ConsoleDbInfoBuilder builder =
        org.doodle.design.idle.console.model.info.ConsoleDbInfo.builder()
            .archiveInfo(fromProto(proto.getArchive()))
            .hostInfo(fromProto(proto.getHost()));
    if (proto.hasMongodb()) {
      builder.mongodbInfo(fromProto(proto.getMongodb()));
    }
    return builder.build();
  }

  public ConsoleDbInfoList toDbInfoList(
      List<org.doodle.design.idle.console.model.info.ConsoleDbInfo> infos) {
    ConsoleDbInfoList.Builder builder = ConsoleDbInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addDb);
    }
    return builder.build();
  }

  public ConsoleGameInfo toProto(org.doodle.design.idle.console.model.info.ConsoleGameInfo info) {
    return ConsoleGameInfo.newBuilder()
        .setArchive(toProto(info.getArchiveInfo()))
        .setHost(toProto(info.getHostInfo()))
        .build();
  }

  public org.doodle.design.idle.console.model.info.ConsoleGameInfo fromProto(
      ConsoleGameInfo proto) {
    return org.doodle.design.idle.console.model.info.ConsoleGameInfo.builder()
        .archiveInfo(fromProto(proto.getArchive()))
        .hostInfo(fromProto(proto.getHost()))
        .build();
  }

  public ConsoleGameInfoList toGameInfoList(
      List<org.doodle.design.idle.console.model.info.ConsoleGameInfo> infos) {
    ConsoleGameInfoList.Builder builder = ConsoleGameInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(infos)) {
      infos.stream().map(this::toProto).forEach(builder::addGame);
    }
    return builder.build();
  }

  public ConsoleComponentQueryReply toComponentQueryError(ConsoleErrorCode errorCode) {
    return ConsoleComponentQueryReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleComponentQueryReply toComponentQueryReply(ConsoleComponentInfo info) {
    return ConsoleComponentQueryReply.newBuilder().setPayload(info).build();
  }

  public ConsoleComponentPageReply toComponentPageError(ConsoleErrorCode errorCode) {
    return ConsoleComponentPageReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleComponentPageReply toComponentPageReply(ConsoleComponentInfoList infoList) {
    return ConsoleComponentPageReply.newBuilder().setPayload(infoList).build();
  }

  public ConsoleCrossQueryReply toCrossQueryError(ConsoleErrorCode errorCode) {
    return ConsoleCrossQueryReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleCrossQueryReply toCrossQueryReply(ConsoleCrossInfo info) {
    return ConsoleCrossQueryReply.newBuilder().setPayload(info).build();
  }

  public ConsoleCrossPageReply toCrossPageError(ConsoleErrorCode errorCode) {
    return ConsoleCrossPageReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleCrossPageReply toCrossPageReply(ConsoleCrossInfoList infoList) {
    return ConsoleCrossPageReply.newBuilder().setPayload(infoList).build();
  }

  public ConsoleDbQueryReply toDbQueryError(ConsoleErrorCode errorCode) {
    return ConsoleDbQueryReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleDbQueryReply toDbQueryReply(ConsoleDbInfo info) {
    return ConsoleDbQueryReply.newBuilder().setPayload(info).build();
  }

  public ConsoleDbPageReply toDbPageError(ConsoleErrorCode errorCode) {
    return ConsoleDbPageReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleDbPageReply toDbPageReply(ConsoleDbInfoList infoList) {
    return ConsoleDbPageReply.newBuilder().setPayload(infoList).build();
  }

  public ConsoleEcsQueryReply toEcsQueryError(ConsoleErrorCode errorCode) {
    return ConsoleEcsQueryReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleEcsQueryReply toEcsQueryReply(ConsoleEcsInfo info) {
    return ConsoleEcsQueryReply.newBuilder().setPayload(info).build();
  }

  public ConsoleEcsPageReply toEcsPageError(ConsoleErrorCode errorCode) {
    return ConsoleEcsPageReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleEcsPageReply toEcsPageReply(ConsoleEcsInfoList infoList) {
    return ConsoleEcsPageReply.newBuilder().setPayload(infoList).build();
  }

  public ConsoleGameQueryReply toGameQueryError(ConsoleErrorCode errorCode) {
    return ConsoleGameQueryReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleGameQueryReply toGameQueryReply(ConsoleGameInfo info) {
    return ConsoleGameQueryReply.newBuilder().setPayload(info).build();
  }

  public ConsoleGamePageReply toGamePageError(ConsoleErrorCode errorCode) {
    return ConsoleGamePageReply.newBuilder().setError(errorCode).build();
  }

  public ConsoleGamePageReply toGamePageReply(ConsoleGameInfoList infoList) {
    return ConsoleGamePageReply.newBuilder().setPayload(infoList).build();
  }
}
