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
package org.doodle.design.messaging.operation.reactive;

import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.reactive.HandlerMethodReturnValueHandler;
import reactor.core.publisher.Mono;

public class OperationReturnHandlerValueHandler implements HandlerMethodReturnValueHandler {

  @Override
  public boolean supportsReturnType(MethodParameter returnType) {
    return true;
  }

  @Override
  public Mono<Void> handleReturnValue(
      Object returnValue, MethodParameter returnType, Message<?> message) {
    return Mono.empty();
  }
}
