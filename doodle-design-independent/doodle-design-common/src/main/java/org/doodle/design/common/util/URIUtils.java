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

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

/** URI 相关的一些辅助操作 */
@UtilityClass
public final class URIUtils {

  /**
   * 从 URI 获取参数并返回 k-v 模式的 map
   *
   * @param uri
   * @return
   */
  public static Map<String, String> getQueryMap(URI uri) {
    String query = uri.getQuery();
    if (!StringUtils.hasLength(query)) {
      return Collections.emptyMap();
    }
    Map<String, String> queryMap = new HashMap<>(2);

    for (String entry : query.split("&")) {
      String[] kv = entry.split("=");
      if (kv.length == 2) { // 不满足正常 "k=v" 格式的直接舍弃
        queryMap.put(kv[0], kv[1]);
      }
    }

    return queryMap;
  }
}
