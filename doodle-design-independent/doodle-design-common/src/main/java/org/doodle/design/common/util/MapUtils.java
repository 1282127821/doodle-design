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
package org.doodle.design.common.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

/** Map 辅助操作 */
@UtilityClass
public final class MapUtils {

  /**
   * 转化为符合 Spring Environment MapPropertySource 需要的 map 格式
   *
   * @param map
   * @return
   */
  public static Map<String, Object> flatten(Map<String, Object> map) {
    return flatten(null, map);
  }

  /**
   * 转化为符合 Spring Environment MapPropertySource 需要的 map 格式
   *
   * @param prefix 前缀
   * @param map
   */
  public static Map<String, Object> flatten(String prefix, Map<String, Object> map) {
    Map<String, Object> result = new LinkedHashMap<>();
    flatten(prefix, result, map);
    return result;
  }

  /**
   * 转化为符合 Spring Environment MapPropertySource 需要的 map 格式
   *
   * @param prefix 前缀
   * @param result 返回 map
   * @param map 输入 map
   */
  public static void flatten(String prefix, Map<String, Object> result, Map<String, Object> map) {
    String namePrefix = (prefix != null) ? prefix + "." : "";
    map.forEach((key, value) -> extract(namePrefix + key, result, value));
  }

  @SuppressWarnings("unchecked")
  private static void extract(String name, Map<String, Object> result, Object value) {
    if (value instanceof Map) {
      if (CollectionUtils.isEmpty((Map<?, ?>) value)) {
        result.put(name, value);
        return;
      }
      flatten(name, result, (Map<String, Object>) value);
    } else if (value instanceof Collection) {
      if (CollectionUtils.isEmpty((Collection<?>) value)) {
        result.put(name, value);
        return;
      }
      int index = 0;
      for (Object object : (Collection<Object>) value) {
        extract(name + "[" + index + "]", result, object);
        index++;
      }
    } else {
      result.put(name, value);
    }
  }
}
