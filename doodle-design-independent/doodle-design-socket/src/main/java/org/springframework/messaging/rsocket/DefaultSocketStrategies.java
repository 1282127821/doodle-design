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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.doodle.design.socket.SocketMetadataExtractor;
import org.doodle.design.socket.SocketStrategies;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.util.RouteMatcher;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DefaultSocketStrategies implements SocketStrategies {
  RSocketStrategies strategies;

  @Override
  public List<Encoder<?>> encoders() {
    return strategies.encoders();
  }

  @Override
  public List<Decoder<?>> decoders() {
    return strategies.decoders();
  }

  @Override
  public RouteMatcher routeMatcher() {
    return strategies.routeMatcher();
  }

  @Override
  public ReactiveAdapterRegistry reactiveAdapterRegistry() {
    return strategies.reactiveAdapterRegistry();
  }

  @Override
  public DataBufferFactory dataBufferFactory() {
    return strategies.dataBufferFactory();
  }

  @Override
  public SocketMetadataExtractor metadataExtractor() {
    return (SocketMetadataExtractor) strategies.metadataExtractor();
  }
}
