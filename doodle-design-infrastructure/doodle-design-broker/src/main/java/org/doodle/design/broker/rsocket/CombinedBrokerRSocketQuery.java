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
package org.doodle.design.broker.rsocket;

import io.rsocket.RSocket;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.doodle.design.bitmap.IndexedMap;
import org.doodle.design.broker.frame.QueryType;
import org.doodle.design.broker.frame.Tags;
import org.springframework.util.CollectionUtils;

@Slf4j
@AllArgsConstructor
public class CombinedBrokerRSocketQuery implements BrokerRSocketQuery {

  private final BrokerRSocketIndex rSocketIndex;

  @Override
  public List<RSocket> query(Tags tags, QueryType queryType) {
    if (Objects.isNull(tags) || CollectionUtils.isEmpty(tags.getTagMap())) {
      throw new IllegalArgumentException("索引查询 TAG 不能为空");
    }
    IndexedMap.QueryOps queryOps =
        (Objects.nonNull(queryType) && queryType == QueryType.XOR)
            ? IndexedMap.QueryOps.XOR
            : IndexedMap.QueryOps.AND;
    return rSocketIndex.query(tags, queryOps);
  }
}
