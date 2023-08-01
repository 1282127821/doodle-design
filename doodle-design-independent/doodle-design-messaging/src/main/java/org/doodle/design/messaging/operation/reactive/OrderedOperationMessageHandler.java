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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class OrderedOperationMessageHandler extends OperationMessageHandler {
  private final List<Object> handlers;

  public OrderedOperationMessageHandler(Supplier<List<Object>> handlerSupplier) {
    this.handlers = handlerSupplier.get();
  }

  @Override
  public Mono<Void> handleAnnotation(Class<? extends Annotation> aClass) {
    Set<Class<?>> classes = getHandlerMap().get(aClass);
    if (CollectionUtils.isEmpty(classes)) {
      return Mono.empty();
    }

    if (Objects.isNull(this.handlers)) {
      return Mono.empty();
    }

    List<Message<?>> messages = new ArrayList<>();
    for (Object handler : this.handlers) {
      if (classes.contains(handler.getClass())) {
        messages.add(createMessage(handler, aClass));
      }
    }
    return Flux.fromIterable(messages).flatMap(this::handleMessage).then();
  }

  static final byte[] EMPTY_PAYLOAD = new byte[0];

  private Message<?> createMessage(Object handler, Class<? extends Annotation> aClass) {
    MessageHeaderAccessor header = new MessageHeaderAccessor();
    header.setHeader(
        DestinationPatternsMessageCondition.LOOKUP_DESTINATION_HEADER,
        obtainRouteMatcher()
            .parseRoute("/" + handler.getClass().getName() + "/" + aClass.getName()));
    return MessageBuilder.createMessage(EMPTY_PAYLOAD, header.getMessageHeaders());
  }
}
