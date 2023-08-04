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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.support.MessageHeaderInitializer;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class OperationRequester {
  OperationMessageHandler messageHandler;

  public RequestSpec annotation(Class<? extends Annotation> annotation) {
    return new RequestSpec(annotation);
  }

  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  public final class RequestSpec {
    Class<? extends Annotation> annotation;
    List<Object> handlers = new ArrayList<>();
    List<MessageHeaderInitializer> headerInitializers = new ArrayList<>();

    RequestSpec(Class<? extends Annotation> annotation) {
      this.annotation = Objects.requireNonNull(annotation);
    }

    public RequestSpec handlers(List<Object> handlers) {
      if (!CollectionUtils.isEmpty(handlers)) {
        this.handlers.clear();
        this.handlers.addAll(handlers);
      }
      return this;
    }

    public RequestSpec header(MessageHeaderInitializer initializer) {
      if (Objects.nonNull(initializer)) {
        this.headerInitializers.add(initializer);
      }
      return this;
    }

    public Mono<Void> natureOrder() {
      return messageHandler.handleAnnotation(this.annotation, this.handlers, headerInitializers);
    }

    public Mono<Void> reversedOrder() {
      return messageHandler.handleAnnotation(this.annotation, this.handlers, headerInitializers);
    }
  }
}
