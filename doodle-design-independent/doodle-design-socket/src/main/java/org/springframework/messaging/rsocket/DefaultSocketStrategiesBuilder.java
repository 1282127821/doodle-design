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
package org.springframework.messaging.rsocket;

import java.util.List;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.doodle.design.socket.SocketStrategies;
import org.doodle.design.socket.SocketStrategiesBuilder;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.util.RouteMatcher;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class DefaultSocketStrategiesBuilder implements SocketStrategiesBuilder {
  DefaultRSocketStrategies.DefaultRSocketStrategiesBuilder builder;

  public DefaultSocketStrategiesBuilder() {
    this.builder = new DefaultRSocketStrategies.DefaultRSocketStrategiesBuilder();
    this.builder.metadataExtractor(new DefaultSocketMetadataExtractor());
  }

  public DefaultSocketStrategiesBuilder(SocketStrategies strategies) {
    this.builder = new DefaultRSocketStrategies.DefaultRSocketStrategiesBuilder(strategies);
    this.builder.metadataExtractor(new DefaultSocketMetadataExtractor());
  }

  @Override
  public SocketStrategiesBuilder encoder(Encoder<?>... encoder) {
    this.builder.encoder(encoder);
    return this;
  }

  @Override
  public SocketStrategiesBuilder encoders(Consumer<List<Encoder<?>>> consumer) {
    this.builder.encoders(consumer);
    return this;
  }

  @Override
  public SocketStrategiesBuilder decoder(Decoder<?>... decoder) {
    this.builder.decoder(decoder);
    return this;
  }

  @Override
  public SocketStrategiesBuilder decoders(Consumer<List<Decoder<?>>> consumer) {
    this.builder.decoders(consumer);
    return this;
  }

  @Override
  public SocketStrategiesBuilder routeMatcher(RouteMatcher routeMatcher) {
    this.builder.routeMatcher(routeMatcher);
    return this;
  }

  @Override
  public SocketStrategiesBuilder reactiveAdapterStrategy(ReactiveAdapterRegistry registry) {
    this.builder.reactiveAdapterStrategy(registry);
    return this;
  }

  @Override
  public SocketStrategiesBuilder dataBufferFactory(DataBufferFactory bufferFactory) {
    this.builder.dataBufferFactory(bufferFactory);
    return this;
  }

  @Override
  public SocketStrategiesBuilder metadataExtractor(MetadataExtractor metadataExtractor) {
    this.builder.metadataExtractor(metadataExtractor);
    return this;
  }

  @Override
  public SocketStrategiesBuilder metadataExtractorRegistry(
      Consumer<MetadataExtractorRegistry> consumer) {
    this.builder.metadataExtractorRegistry(consumer);
    return this;
  }

  @Override
  public SocketStrategies build() {
    RSocketStrategies strategies = this.builder.build();
    return new DefaultSocketStrategies(strategies);
  }
}
