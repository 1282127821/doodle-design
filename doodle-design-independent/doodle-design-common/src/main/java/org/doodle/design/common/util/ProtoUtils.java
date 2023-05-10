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

import com.google.protobuf.ListValue;
import com.google.protobuf.NullValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import java.util.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ProtoUtils {
  public static Object fromProto(Value any) {
    switch (any.getKindCase()) {
      case NULL_VALUE -> {
        return null;
      }
      case BOOL_VALUE -> {
        return any.getBoolValue();
      }
      case NUMBER_VALUE -> {
        return any.getNumberValue();
      }
      case STRING_VALUE -> {
        return any.getStringValue();
      }
      case STRUCT_VALUE -> {
        Struct struct = any.getStructValue();
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Value> pair : struct.getFieldsMap().entrySet()) {
          map.put(pair.getKey(), fromProto(pair.getValue()));
        }
        return map;
      }
      case LIST_VALUE -> {
        List<Object> list = new ArrayList<>();
        for (Value val : any.getListValue().getValuesList()) {
          list.add(fromProto(val));
        }
        return list;
      }
      default -> throw new ClassCastException("不支持转换类型: " + any);
    }
  }

  @SuppressWarnings("unchecked")
  public static Value toProto(Object val) {
    Value.Builder builder = Value.newBuilder();

    if (Objects.isNull(val)) {
      builder.setNullValue(NullValue.NULL_VALUE);
    } else if (val instanceof Boolean) {
      builder.setBoolValue((Boolean) val);
    } else if (val instanceof Double) {
      builder.setNumberValue((Double) val);
    } else if (val instanceof String) {
      builder.setStringValue((String) val);
    } else if (val instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) val;
      Struct.Builder struct = Struct.newBuilder();
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        struct.putFields(entry.getKey(), toProto(entry.getValue()));
      }
      builder.setStructValue(struct.build());
    } else if (val instanceof List) {
      ListValue.Builder list = ListValue.newBuilder();
      for (Object obj : (List<Object>) val) {
        list.addValues(toProto(obj));
      }
      builder.setListValue(list.build());
    } else {
      throw new ClassCastException("不支持转换该类型: " + val);
    }
    return builder.build();
  }
}
