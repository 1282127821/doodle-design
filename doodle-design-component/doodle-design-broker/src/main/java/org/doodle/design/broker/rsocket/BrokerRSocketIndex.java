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
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.doodle.broker.design.frame.Tags;
import org.doodle.broker.design.frame.UUID;
import org.doodle.design.bitmap.IndexedMap;

@Slf4j
public class BrokerRSocketIndex implements IndexedMap<UUID, RSocket, Tags> {

  private final BrokerRSocketIndexedMap indexedMap = new BrokerRSocketIndexedMap();

  @Override
  public RSocket get(UUID key) {
    return this.indexedMap.get(key);
  }

  @Override
  public RSocket put(UUID key, RSocket value, Tags tags) {
    log.info("增加 RSocket UUID: {} Tags: {}", key, tags);
    return this.indexedMap.put(key, value, tags.getTagMap());
  }

  @Override
  public RSocket remove(UUID key) {
    log.info("删除 RSocket UUID: {}", key);
    return this.indexedMap.remove(key);
  }

  @Override
  public int size() {
    return indexedMap.size();
  }

  @Override
  public boolean isEmpty() {
    return this.indexedMap.isEmpty();
  }

  @Override
  public void clear() {
    this.indexedMap.clear();
  }

  @Override
  public Collection<RSocket> values() {
    return this.indexedMap.values();
  }

  @Override
  public List<RSocket> query(Tags tags) {
    List<RSocket> query = this.indexedMap.query(tags.getTagMap());
    log.info("索引检索 TAG: {} 结果: {}", tags, query);
    return query;
  }
}
