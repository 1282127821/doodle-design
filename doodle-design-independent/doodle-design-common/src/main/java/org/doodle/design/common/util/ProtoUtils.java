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

import com.google.protobuf.*;
import java.time.Instant;
import java.util.*;
import lombok.experimental.UtilityClass;
import org.doodle.design.common.Result;
import org.doodle.design.common.Status;

@UtilityClass
public final class ProtoUtils {

  public static Instant fromProto(Timestamp any) {
    return Instant.ofEpochSecond(any.getSeconds(), any.getNanos());
  }

  public static Timestamp toProto(Instant val) {
    return Timestamp.newBuilder().setSeconds(val.getEpochSecond()).setNanos(val.getNano()).build();
  }

  public static <T> Result<T> fromProto(Status any) {
    return Result.code(any.getCode(), any.getMessage()).body(null);
  }

  public static <T> Status toProto(Result<T> val) {
    return Status.newBuilder().setCode(val.getCode()).setMessage(val.getMessage()).build();
  }

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
        return fromProto(any.getStructValue());
      }
      case LIST_VALUE -> {
        return fromProto(any.getListValue());
      }
      default -> throw new ClassCastException("不支持转换类型: " + any);
    }
  }

  public static Map<String, Object> fromProto(Struct struct) {
    Map<String, Object> map = new HashMap<>();
    for (Map.Entry<String, Value> pair : struct.getFieldsMap().entrySet()) {
      map.put(pair.getKey(), fromProto(pair.getValue()));
    }
    return map;
  }

  public static List<Object> fromProto(ListValue listValue) {
    List<Object> list = new ArrayList<>();
    for (Value val : listValue.getValuesList()) {
      list.add(fromProto(val));
    }
    return list;
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
      builder.setStructValue(toProto(((Map<String, Object>) val)));
    } else if (val instanceof List) {
      builder.setListValue(toProto((List<Object>) val));
    } else {
      throw new ClassCastException("不支持转换该类型: " + val);
    }
    return builder.build();
  }

  public static Struct toProto(Map<String, Object> map) {
    Struct.Builder builder = Struct.newBuilder();
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      builder.putFields(entry.getKey(), toProto(entry.getValue()));
    }
    return builder.build();
  }

  public static ListValue toProto(List<Object> list) {
    ListValue.Builder builder = ListValue.newBuilder();
    for (Object obj : list) {
      builder.addValues(toProto(obj));
    }
    return builder.build();
  }
}
