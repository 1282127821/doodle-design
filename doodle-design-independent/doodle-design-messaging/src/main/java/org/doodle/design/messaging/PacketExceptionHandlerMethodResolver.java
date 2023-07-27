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
package org.doodle.design.messaging;

import java.lang.reflect.Method;
import java.util.*;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.messaging.handler.invocation.AbstractExceptionHandlerMethodResolver;

public class PacketExceptionHandlerMethodResolver extends AbstractExceptionHandlerMethodResolver {

  public PacketExceptionHandlerMethodResolver(Class<?> handlerType) {
    super(initExceptionMappings(handlerType));
  }

  private static Map<Class<? extends Throwable>, Method> initExceptionMappings(
      Class<?> handlerType) {
    Map<Method, PacketExceptionHandler> methods =
        MethodIntrospector.selectMethods(
            handlerType,
            (MethodIntrospector.MetadataLookup<PacketExceptionHandler>)
                method ->
                    AnnotatedElementUtils.findMergedAnnotation(
                        method, PacketExceptionHandler.class));

    Map<Class<? extends Throwable>, Method> result = new HashMap<>();
    for (Map.Entry<Method, PacketExceptionHandler> entry : methods.entrySet()) {
      Method method = entry.getKey();
      List<Class<? extends Throwable>> exceptionTypes =
          new ArrayList<>(Arrays.asList(entry.getValue().value()));
      if (exceptionTypes.isEmpty()) {
        exceptionTypes.addAll(getExceptionsFromMethodSignature(method));
      }
      for (Class<? extends Throwable> exceptionType : exceptionTypes) {
        Method oldMethod = result.put(exceptionType, method);
        if (oldMethod != null && !oldMethod.equals(method)) {
          throw new IllegalStateException(
              "重复的 @PacketExceptionHandler 异常处理方法: ["
                  + exceptionType
                  + "]: {"
                  + oldMethod
                  + ", "
                  + method
                  + "}");
        }
      }
    }
    return result;
  }
}
