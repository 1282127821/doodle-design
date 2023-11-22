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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.agrona.collections.Int2ObjectHashMap;
import org.agrona.collections.Object2ObjectHashMap;
import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;

public class RoaringBitmapIndexedMap<K, V> implements IndexedMap<K, V, Map<String, String>> {
  private final AtomicInteger internalIndex = new AtomicInteger();
  private final Map<K, Integer> keyToIndex;
  private final Int2ObjectHashMap<V> indexToValue;
  private final Int2ObjectHashMap<Map<String, String>> indexToTags;
  private final Table<String, CharSequence, RoaringBitmap> tagIndexes;

  public RoaringBitmapIndexedMap() {
    this.keyToIndex = Collections.synchronizedMap(new HashMap<>());
    this.indexToValue = new Int2ObjectHashMap<>();
    this.indexToTags = new Int2ObjectHashMap<>();
    this.tagIndexes = new Table<>(new Object2ObjectHashMap<>(), Object2ObjectHashMap::new);
  }

  @Override
  public V get(K key) {
    Integer index = keyToIndex.get(key);
    return Objects.nonNull(index) ? indexToValue.get(index) : null;
  }

  @Override
  public V put(K key, V value, Map<String, String> tags) {
    V previousValue;
    int index = keyToIndex.computeIfAbsent(key, ignored -> internalIndex.incrementAndGet());
    synchronized (this) {
      previousValue = indexToValue.put(index, value);
      Map<String, String> previousTags = indexToTags.put(index, tags);
      if (Objects.nonNull(previousTags) && !previousTags.isEmpty()) {
        removeTags(index, previousTags, tags);
      }
      addTags(index, tags);
    }
    return previousValue;
  }

  @Override
  public V remove(K key) {
    Integer index = keyToIndex.remove(key);
    if (index != null) {
      V previousValue = indexToValue.remove(index);
      synchronized (this) {
        Map<String, String> tags = indexToTags.remove(index);
        if (Objects.nonNull(tags)) {
          removeTags(index, tags, Collections.emptyMap());
        }
      }
      return previousValue;
    }
    return null;
  }

  private void addTags(int index, Map<String, String> tags) {
    for (Map.Entry<String, String> tag : tags.entrySet()) {
      RoaringBitmap bitmap = tagIndexes.get(tag.getKey(), tag.getValue());
      if (Objects.isNull(bitmap)) {
        bitmap = new RoaringBitmap();
        tagIndexes.put(tag.getKey(), tag.getValue(), bitmap);
      }
      bitmap.add(index);
    }
  }

  private void removeTags(int index, Map<String, String> removed, Map<String, String> reserved) {
    for (Map.Entry<String, String> tag : removed.entrySet()) {
      String previousValue = reserved.get(tag.getKey());
      if (Objects.nonNull(previousValue) && !previousValue.equalsIgnoreCase(tag.getValue())) {
        RoaringBitmap bitmap = tagIndexes.get(tag.getKey(), tag.getValue());
        if (Objects.nonNull(bitmap)) {
          bitmap.remove(index);
          if (bitmap.isEmpty()) {
            tagIndexes.remove(tag.getKey(), tag.getValue());
          }
        }
      }
    }
  }

  @Override
  public int size() {
    return indexToValue.size();
  }

  @Override
  public boolean isEmpty() {
    return indexToValue.isEmpty();
  }

  @Override
  public void clear() {
    synchronized (this) {
      keyToIndex.clear();
      indexToValue.clear();
      indexToTags.clear();
    }
  }

  @Override
  public Collection<V> values() {
    return indexToValue.values();
  }

  @Override
  public List<V> query(Map<String, String> tags, QueryOps queryOps) {
    if (Objects.isNull(tags) || tags.isEmpty()) {
      return new ArrayList<>(indexToValue.values());
    }
    RoaringBitmap result = null;
    for (Map.Entry<String, String> tag : tags.entrySet()) {
      RoaringBitmap bitmap = tagIndexes.get(tag.getKey(), tag.getValue());
      if (Objects.isNull(bitmap)) {
        return Collections.emptyList();
      }
      if (Objects.isNull(result)) {
        result = new RoaringBitmap();
        result.or(bitmap);
      } else {
        if (queryOps == QueryOps.AND) {
          result.and(bitmap);
        } else if (queryOps == QueryOps.OR) {
          result.or(bitmap);
        }
      }

      if (result.isEmpty()) {
        return Collections.emptyList();
      }
    }
    return new RoaringBitmapList(result);
  }

  private class RoaringBitmapList extends AbstractList<V> {
    private final RoaringBitmap result;

    public RoaringBitmapList(RoaringBitmap result) {
      this.result = result;
    }

    @Override
    public V get(int index) {
      int key = result.select(index);
      return indexToValue.get(key);
    }

    @Override
    public Iterator<V> iterator() {
      return new RoaringBitmapIterator(result.getIntIterator());
    }

    @Override
    public int size() {
      return result.getCardinality();
    }
  }

  private class RoaringBitmapIterator implements Iterator<V> {
    private final IntIterator results;

    public RoaringBitmapIterator(IntIterator results) {
      this.results = results;
    }

    @Override
    public boolean hasNext() {
      return results.hasNext();
    }

    @Override
    public V next() {
      int index = results.next();
      return indexToValue.get(index);
    }
  }
}
