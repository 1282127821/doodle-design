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
package org.doodle.design.security;

import java.util.*;
import org.doodle.design.common.ProtoMapper;
import org.springframework.util.CollectionUtils;

public abstract class SecurityMapper implements ProtoMapper {

  public AuthorityInfo toProto(org.doodle.design.security.model.info.AuthorityInfo info) {
    return AuthorityInfo.newBuilder().setAuthority(info.getAuthority()).build();
  }

  public org.doodle.design.security.model.info.AuthorityInfo fromProto(AuthorityInfo proto) {
    return org.doodle.design.security.model.info.AuthorityInfo.builder()
        .authority(proto.getAuthority())
        .build();
  }

  public AuthorityInfoList toAuthorityInfoListProto(
      List<org.doodle.design.security.model.info.AuthorityInfo> authorityInfos) {
    AuthorityInfoList.Builder builder = AuthorityInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(authorityInfos)) {
      authorityInfos.stream().map(this::toProto).forEach(builder::addAuthorityInfo);
    }
    return builder.build();
  }

  public List<org.doodle.design.security.model.info.AuthorityInfo> fromProtoList(
      AuthorityInfoList proto) {
    return !CollectionUtils.isEmpty(proto.getAuthorityInfoList())
        ? proto.getAuthorityInfoList().stream().map(this::fromProto).toList()
        : Collections.emptyList();
  }

  public RoleInfo toProto(org.doodle.design.security.model.info.RoleInfo info) {
    RoleInfo.Builder builder = RoleInfo.newBuilder().setRole(info.getRole());
    info.getAuthorities().forEach(a -> builder.addAuthorities(toProto(a)));
    return builder.build();
  }

  public RoleInfoList toRoleInfoListProto(
      List<org.doodle.design.security.model.info.RoleInfo> roleInfos) {
    RoleInfoList.Builder builder = RoleInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(roleInfos)) {
      roleInfos.stream().map(this::toProto).forEach(builder::addRoleInfo);
    }
    return builder.build();
  }

  public List<org.doodle.design.security.model.info.RoleInfo> fromProtoList(RoleInfoList proto) {
    return !CollectionUtils.isEmpty(proto.getRoleInfoList())
        ? proto.getRoleInfoList().stream().map(this::fromProto).toList()
        : Collections.emptyList();
  }

  public org.doodle.design.security.model.info.RoleInfo fromProto(RoleInfo proto) {
    org.doodle.design.security.model.info.RoleInfo.RoleInfoBuilder builder =
        org.doodle.design.security.model.info.RoleInfo.builder().role(proto.getRole());
    if (!CollectionUtils.isEmpty(proto.getAuthoritiesList())) {
      Set<org.doodle.design.security.model.info.AuthorityInfo> authorities = new HashSet<>();
      for (AuthorityInfo info : proto.getAuthoritiesList()) {
        authorities.add(fromProto(info));
      }
      builder.authorities(authorities);
    }
    return builder.build();
  }

  public UserInfo toProto(org.doodle.design.security.model.info.UserInfo info) {
    UserInfo.Builder builder =
        UserInfo.newBuilder().setUsername(info.getUsername()).setEnable(info.isEnable());
    info.getRoles().forEach(r -> builder.addRoles(toProto(r)));
    return builder.build();
  }

  public org.doodle.design.security.model.info.UserInfo fromProto(UserInfo proto) {
    org.doodle.design.security.model.info.UserInfo.UserInfoBuilder builder =
        org.doodle.design.security.model.info.UserInfo.builder()
            .username(proto.getUsername())
            .enable(proto.getEnable());
    if (!CollectionUtils.isEmpty(proto.getRolesList())) {
      Set<org.doodle.design.security.model.info.RoleInfo> roleInfos = new HashSet<>();
      for (RoleInfo info : proto.getRolesList()) {
        roleInfos.add(fromProto(info));
      }
      builder.roles(roleInfos);
    }
    return builder.build();
  }

  public UserInfoList toUserInfoProtoList(
      List<org.doodle.design.security.model.info.UserInfo> userInfos) {
    UserInfoList.Builder builder = UserInfoList.newBuilder();
    if (!CollectionUtils.isEmpty(userInfos)) {
      userInfos.stream().map(this::toProto).forEach(builder::addUserInfo);
    }
    return builder.build();
  }

  public List<org.doodle.design.security.model.info.UserInfo> fromProtoList(UserInfoList proto) {
    return !CollectionUtils.isEmpty(proto.getUserInfoList())
        ? proto.getUserInfoList().stream().map(this::fromProto).toList()
        : Collections.emptyList();
  }

  public SecurityUserDetailsQueryReply toProto(UserDetailsInfo info) {
    return SecurityUserDetailsQueryReply.newBuilder().setPayload(info).build();
  }

  public SecurityUserQueryReply toProto(UserInfo userInfo) {
    return SecurityUserQueryReply.newBuilder().setPayload(userInfo).build();
  }

  public SecurityUserPageReply toProto(UserInfoList list) {
    return SecurityUserPageReply.newBuilder().setPayload(list).build();
  }

  public SecurityRoleQueryReply toProto(RoleInfo info) {
    return SecurityRoleQueryReply.newBuilder().setPayload(info).build();
  }

  public SecurityRolePageReply toProto(RoleInfoList list) {
    return SecurityRolePageReply.newBuilder().setPayload(list).build();
  }

  public SecurityAuthorityQueryReply toProto(AuthorityInfo info) {
    return SecurityAuthorityQueryReply.newBuilder().setPayload(info).build();
  }

  public SecurityAuthorityPageReply toProto(AuthorityInfoList list) {
    return SecurityAuthorityPageReply.newBuilder().setPayload(list).build();
  }
}
