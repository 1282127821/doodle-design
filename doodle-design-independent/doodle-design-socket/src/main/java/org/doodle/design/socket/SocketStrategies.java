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

import org.springframework.messaging.rsocket.DefaultSocketStrategiesBuilder;
import org.springframework.messaging.rsocket.RSocketStrategies;

public interface SocketStrategies extends RSocketStrategies {

  @Override
  SocketMetadataExtractor metadataExtractor();

  static SocketStrategies create() {
    return new DefaultSocketStrategiesBuilder().build();
  }

  @Override
  default SocketStrategiesBuilder mutate() {
    return new DefaultSocketStrategiesBuilder(this);
  }
}
