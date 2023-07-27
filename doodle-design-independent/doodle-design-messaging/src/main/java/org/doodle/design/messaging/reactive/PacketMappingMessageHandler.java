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
package org.doodle.design.messaging.reactive;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.doodle.design.messaging.PacketAdviceBean;
import org.doodle.design.messaging.PacketDeliveryException;
import org.doodle.design.messaging.PacketExceptionHandlerMethodResolver;
import org.doodle.design.messaging.PacketMapping;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.KotlinDetector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.codec.Decoder;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.CompositeMessageCondition;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.messaging.handler.annotation.reactive.*;
import org.springframework.messaging.handler.invocation.AbstractExceptionHandlerMethodResolver;
import org.springframework.messaging.handler.invocation.reactive.AbstractMethodMessageHandler;
import org.springframework.messaging.handler.invocation.reactive.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.reactive.HandlerMethodReturnValueHandler;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.RouteMatcher;
import org.springframework.util.SimpleRouteMatcher;
import org.springframework.validation.Validator;
import reactor.core.publisher.Mono;

public class PacketMappingMessageHandler
    extends AbstractMethodMessageHandler<CompositeMessageCondition> {

  private final List<Decoder<?>> decoders = new ArrayList<>();

  @Nullable private Validator validator;

  @Nullable private RouteMatcher routeMatcher;

  @Getter private ConversionService conversionService = new DefaultFormattingConversionService();

  public PacketMappingMessageHandler() {
    setHandlerPredicate(type -> AnnotatedElementUtils.hasAnnotation(type, Controller.class));
  }

  public void setDecoders(List<? extends Decoder<?>> decoders) {
    this.decoders.clear();
    this.decoders.addAll(decoders);
  }

  public List<? extends Decoder<?>> getDecoders() {
    return this.decoders;
  }

  public void setValidator(@Nullable Validator validator) {
    this.validator = validator;
  }

  @Nullable
  public Validator getValidator() {
    return this.validator;
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

  public void registerMessagingAdvice(PacketAdviceBean bean) {
    Class<?> type = bean.getBeanType();
    if (type != null) {
      PacketExceptionHandlerMethodResolver resolver =
          new PacketExceptionHandlerMethodResolver(type);
      if (resolver.hasExceptionMappings()) {
        registerExceptionHandlerAdvice(bean, resolver);
        if (logger.isTraceEnabled()) {
          logger.trace("Detected @PacketExceptionHandler methods in " + bean);
        }
      }
    }
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

    resolvers.add(
        new PayloadMethodArgumentResolver(
            getDecoders(), this.validator, getReactiveAdapterRegistry(), true));

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

  @Nullable
  protected CompositeMessageCondition getCondition(AnnotatedElement element) {
    PacketMapping ann = AnnotatedElementUtils.findMergedAnnotation(element, PacketMapping.class);
    if (ann == null) {
      return null;
    }
    return new CompositeMessageCondition(
        new DestinationPatternsMessageCondition(
            new String[] {String.valueOf(ann.value())}, obtainRouteMatcher()));
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
    return new PacketExceptionHandlerMethodResolver(beanType);
  }

  @Override
  protected Mono<Void> handleMatch(
      CompositeMessageCondition mapping, HandlerMethod handlerMethod, Message<?> message) {
    return super.handleMatch(mapping, handlerMethod, message);
  }

  @Override
  protected void handleNoMatch(RouteMatcher.Route destination, Message<?> message) {
    throw new PacketDeliveryException(message, "找不到对应路由目的地: {}" + destination);
  }
}
