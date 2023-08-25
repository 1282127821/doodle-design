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
package org.doodle.design.socket;

import io.rsocket.frame.SocketFrameType;
import java.util.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.AbstractMessageCondition;
import org.springframework.util.CollectionUtils;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketFrameTypeMessageCondition
    extends AbstractMessageCondition<SocketFrameTypeMessageCondition> {

  public static final String FRAME_TYPE_HEADER = "socketFrameType";

  public static final SocketFrameTypeMessageCondition CONNECT_CONDITION =
      new SocketFrameTypeMessageCondition(SocketFrameType.SETUP);

  public static final SocketFrameTypeMessageCondition ONEWAY_CONDITION =
      new SocketFrameTypeMessageCondition(SocketFrameType.ONEWAY);

  public static final SocketFrameTypeMessageCondition EMPTY_CONDITION =
      new SocketFrameTypeMessageCondition();

  private static final Map<String, SocketFrameTypeMessageCondition> frameTypeConditionCache;

  static {
    frameTypeConditionCache = CollectionUtils.newHashMap(SocketFrameType.values().length);
    for (SocketFrameType type : SocketFrameType.values()) {
      frameTypeConditionCache.put(type.name(), new SocketFrameTypeMessageCondition(type));
    }
  }

  @Getter Set<SocketFrameType> frameTypes;

  public SocketFrameTypeMessageCondition(SocketFrameType... frameTypes) {
    this(Arrays.asList(frameTypes));
  }

  public SocketFrameTypeMessageCondition(Collection<SocketFrameType> frameTypes) {
    this.frameTypes = Collections.unmodifiableSet(new LinkedHashSet<>(frameTypes));
  }

  private SocketFrameTypeMessageCondition() {
    this.frameTypes = Collections.emptySet();
  }

  @Override
  protected Collection<?> getContent() {
    return frameTypes;
  }

  @Override
  protected String getToStringInfix() {
    return " || ";
  }

  public static SocketFrameType getFrameType(Message<?> message) {
    return (SocketFrameType) message.getHeaders().get(FRAME_TYPE_HEADER);
  }

  @Override
  public SocketFrameTypeMessageCondition combine(SocketFrameTypeMessageCondition other) {
    if (this.frameTypes.equals(other.frameTypes)) {
      return other;
    }
    LinkedHashSet<SocketFrameType> set = new LinkedHashSet<>(this.frameTypes);
    set.addAll(other.frameTypes);
    return new SocketFrameTypeMessageCondition(set);
  }

  @Override
  public SocketFrameTypeMessageCondition getMatchingCondition(Message<?> message) {
    SocketFrameType actual = message.getHeaders().get(FRAME_TYPE_HEADER, SocketFrameType.class);
    if (actual != null) {
      for (SocketFrameType frameType : this.frameTypes) {
        if (actual == frameType) {
          return frameTypeConditionCache.get(frameType.name());
        }
      }
    }
    return null;
  }

  @Override
  public int compareTo(SocketFrameTypeMessageCondition other, Message<?> message) {
    return other.frameTypes.size() - this.frameTypes.size();
  }
}
