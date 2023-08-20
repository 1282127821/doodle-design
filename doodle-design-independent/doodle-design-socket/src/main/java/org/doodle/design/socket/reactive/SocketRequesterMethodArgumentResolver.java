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
package org.doodle.design.socket.reactive;

import io.rsocket.Socket;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.reactive.HandlerMethodArgumentResolver;
import reactor.core.publisher.Mono;

public final class SocketRequesterMethodArgumentResolver implements HandlerMethodArgumentResolver {
  public static final String SOCKET_REQUESTER_HEADER = "socketRequester";

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    Class<?> type = parameter.getParameterType();
    return Socket.class.equals(type);
  }

  @Override
  public Mono<Object> resolveArgument(MethodParameter parameter, Message<?> message) {
    Object headerValue = message.getHeaders().get(SOCKET_REQUESTER_HEADER);
    Class<?> type = parameter.getParameterType();
    if (Socket.class.equals(type)) {
      return Mono.justOrEmpty(headerValue);
    }
    return Mono.empty();
  }
}
