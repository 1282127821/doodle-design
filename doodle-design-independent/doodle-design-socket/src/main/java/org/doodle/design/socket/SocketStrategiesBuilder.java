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
package org.doodle.design.socket;

import java.util.List;
import java.util.function.Consumer;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.messaging.rsocket.MetadataExtractorRegistry;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.RouteMatcher;

public interface SocketStrategiesBuilder extends RSocketStrategies.Builder {
  @Override
  SocketStrategiesBuilder encoder(Encoder<?>... encoder);

  @Override
  SocketStrategiesBuilder encoders(Consumer<List<Encoder<?>>> consumer);

  @Override
  SocketStrategiesBuilder decoder(Decoder<?>... decoder);

  @Override
  SocketStrategiesBuilder decoders(Consumer<List<Decoder<?>>> consumer);

  @Override
  SocketStrategiesBuilder routeMatcher(RouteMatcher routeMatcher);

  @Override
  SocketStrategiesBuilder reactiveAdapterStrategy(ReactiveAdapterRegistry registry);

  @Override
  SocketStrategiesBuilder dataBufferFactory(DataBufferFactory bufferFactory);

  @Override
  SocketStrategiesBuilder metadataExtractor(MetadataExtractor metadataExtractor);

  @Override
  SocketStrategiesBuilder metadataExtractorRegistry(Consumer<MetadataExtractorRegistry> consumer);

  @Override
  SocketStrategies build();
}
