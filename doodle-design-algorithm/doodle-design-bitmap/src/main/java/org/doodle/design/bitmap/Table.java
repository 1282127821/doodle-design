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
package org.doodle.design.bitmap;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString(exclude = "factory")
@RequiredArgsConstructor
class Table<R, C, V> {
  final Map<R, Map<C, V>> backingMap;
  final Supplier<? extends Map<C, V>> factory;

  public boolean contains(Object rowKey, Object columnKey) {
    return Objects.nonNull(rowKey) && Objects.nonNull(columnKey) && doContains(rowKey, columnKey);
  }

  public V get(Object rowKey, Object columnKey) {
    return (Objects.isNull(rowKey) || Objects.isNull(columnKey)) ? null : doGet(rowKey, columnKey);
  }

  public boolean isEmpty() {
    return backingMap.isEmpty();
  }

  public int size() {
    int size = 0;
    for (Map<C, V> map : backingMap.values()) {
      size += map.size();
    }
    return size;
  }

  public void clear() {
    backingMap.clear();
  }

  private Map<C, V> getOrCreate(R rowKey) {
    Map<C, V> map = backingMap.get(rowKey);
    if (Objects.isNull(map)) {
      map = factory.get();
      backingMap.put(rowKey, map);
    }
    return map;
  }

  public V put(R rowKey, C columnKey, V value) {
    Objects.requireNonNull(rowKey);
    Objects.requireNonNull(columnKey);
    Objects.requireNonNull(value);
    return getOrCreate(rowKey).put(columnKey, value);
  }

  public V remove(Object rowKey, Object columnKey) {
    if (Objects.isNull(rowKey) || Objects.isNull(columnKey)) {
      return null;
    }
    Map<C, V> map = safeGet(backingMap, rowKey);
    if (Objects.isNull(map)) {
      return null;
    }
    V value = map.remove(columnKey);
    if (map.isEmpty()) {
      backingMap.remove(rowKey);
    }
    return value;
  }

  public Map<C, V> row(R rowKey) {
    return backingMap.get(rowKey);
  }

  public Map<R, Map<C, V>> rowMap() {
    return backingMap;
  }

  protected boolean doContains(Object rowKey, Object columnKey) {
    Map<C, V> row = safeGet(rowMap(), rowKey);
    return Objects.nonNull(row) && safeContainsKey(row, columnKey);
  }

  protected V doGet(Object rowKey, Object columnKey) {
    Map<C, V> row = safeGet(rowMap(), rowKey);
    return Objects.isNull(row) ? null : safeGet(row, columnKey);
  }

  static boolean safeContainsKey(Map<?, ?> map, Object key) {
    Objects.requireNonNull(map);
    try {
      return map.containsKey(key);
    } catch (ClassCastException | NullPointerException e) {
      return false;
    }
  }

  static <V> V safeGet(Map<?, V> map, Object key) {
    Objects.requireNonNull(map);
    try {
      return map.get(key);
    } catch (ClassCastException | NullPointerException e) {
      return null;
    }
  }

  static <V> V safeRemove(Map<?, V> map, Object key) {
    Objects.requireNonNull(map);
    try {
      return map.remove(key);
    } catch (ClassCastException | NullPointerException e) {
      return null;
    }
  }
}
