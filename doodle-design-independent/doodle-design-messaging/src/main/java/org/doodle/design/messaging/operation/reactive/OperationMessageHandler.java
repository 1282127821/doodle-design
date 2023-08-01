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
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.KotlinDetector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.CompositeMessageCondition;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.handler.annotation.reactive.*;
import org.springframework.messaging.handler.invocation.AbstractExceptionHandlerMethodResolver;
import org.springframework.messaging.handler.invocation.reactive.AbstractMethodMessageHandler;
import org.springframework.messaging.handler.invocation.reactive.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.reactive.HandlerMethodReturnValueHandler;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.RouteMatcher;
import org.springframework.util.SimpleRouteMatcher;
import reactor.core.publisher.Mono;

public abstract class OperationMessageHandler
    extends AbstractMethodMessageHandler<CompositeMessageCondition> {

  private final List<Class<? extends Annotation>> annotations = new ArrayList<>();

  @Getter
  private final Map<Class<? extends Annotation>, Set<Class<?>>> handlerMap = new HashMap<>();

  @Nullable private RouteMatcher routeMatcher;

  @Getter private ConversionService conversionService = new DefaultFormattingConversionService();

  public void setAnnotations(List<Class<? extends Annotation>> annotations) {
    this.annotations.clear();
    this.annotations.addAll(annotations);
  }

  public void setRouteMatcher(@Nullable RouteMatcher routeMatcher) {
    this.routeMatcher = routeMatcher;
  }

  @Nullable
  public RouteMatcher getRouteMatcher() {
    return this.routeMatcher;
  }

  protected RouteMatcher obtainRouteMatcher() {
    RouteMatcher routeMatcher = getRouteMatcher();
    Assert.state(routeMatcher != null, "No RouteMatcher set");
    return routeMatcher;
  }

  public void setConversionService(ConversionService conversionService) {
    this.conversionService = conversionService;
  }

  @Override
  public void afterPropertiesSet() {

    if (this.routeMatcher == null) {
      AntPathMatcher pathMatcher = new AntPathMatcher();
      pathMatcher.setPathSeparator(".");
      this.routeMatcher = new SimpleRouteMatcher(pathMatcher);
    }

    super.afterPropertiesSet();
  }

  @Override
  protected List<? extends HandlerMethodArgumentResolver> initArgumentResolvers() {
    List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

    ApplicationContext context = getApplicationContext();
    ConfigurableBeanFactory beanFactory =
        (context instanceof ConfigurableApplicationContext cac ? cac.getBeanFactory() : null);

    resolvers.add(new HeaderMethodArgumentResolver(this.conversionService, beanFactory));
    resolvers.add(new HeadersMethodArgumentResolver());

    if (KotlinDetector.isKotlinPresent()) {
      resolvers.add(new ContinuationHandlerMethodArgumentResolver());
    }

    resolvers.addAll(getArgumentResolverConfigurer().getCustomResolvers());

    return resolvers;
  }

  @Override
  protected List<? extends HandlerMethodReturnValueHandler> initReturnValueHandlers() {
    return new ArrayList<>(getReturnValueHandlerConfigurer().getCustomHandlers());
  }

  @Override
  protected CompositeMessageCondition getMappingForMethod(Method method, Class<?> handlerType) {
    CompositeMessageCondition methodCondition = getCondition(method);
    if (methodCondition != null) {
      CompositeMessageCondition typeCondition = getCondition(handlerType);
      if (typeCondition != null) {
        return typeCondition.combine(methodCondition);
      }
    }
    return methodCondition;
  }

  protected abstract CompositeMessageCondition getOperationCondition(AnnotatedElement element);

  public abstract Mono<Void> handleAnnotation(Class<? extends Annotation> aClass);

  @Nullable
  protected CompositeMessageCondition getCondition(AnnotatedElement element) {
    CompositeMessageCondition operationCondition = getOperationCondition(element);
    if (Objects.nonNull(operationCondition)) {
      return operationCondition;
    }

    for (Class<? extends Annotation> annotation : this.annotations) {
      CompositeMessageCondition condition = getCondition(element, annotation);
      if (Objects.nonNull(condition)) {
        return condition;
      }
    }

    return null;
  }

  protected <A extends Annotation> CompositeMessageCondition getCondition(
      AnnotatedElement element, Class<A> aClass) {
    if (Objects.nonNull(AnnotatedElementUtils.findMergedAnnotation(element, aClass))) {
      handlerMap
          .computeIfAbsent(aClass, key -> new HashSet<>())
          .add(((Method) element).getDeclaringClass());
      return new CompositeMessageCondition(
          new DestinationPatternsMessageCondition(aClass.getName()));
    }
    return null;
  }

  @Override
  protected Set<String> getDirectLookupMappings(CompositeMessageCondition mapping) {
    Set<String> result = new LinkedHashSet<>();
    for (String pattern :
        mapping.getCondition(DestinationPatternsMessageCondition.class).getPatterns()) {
      if (!obtainRouteMatcher().isPattern(pattern)) {
        result.add(pattern);
      }
    }
    return result;
  }

  @Override
  protected RouteMatcher.Route getDestination(Message<?> message) {
    return (RouteMatcher.Route)
        message.getHeaders().get(DestinationPatternsMessageCondition.LOOKUP_DESTINATION_HEADER);
  }

  @Override
  protected CompositeMessageCondition getMatchingMapping(
      CompositeMessageCondition mapping, Message<?> message) {
    return mapping.getMatchingCondition(message);
  }

  @Override
  protected Comparator<CompositeMessageCondition> getMappingComparator(Message<?> message) {
    return (info1, info2) -> info1.compareTo(info2, message);
  }

  @Override
  protected AbstractExceptionHandlerMethodResolver createExceptionMethodResolverFor(
      Class<?> beanType) {
    return null;
  }
}
