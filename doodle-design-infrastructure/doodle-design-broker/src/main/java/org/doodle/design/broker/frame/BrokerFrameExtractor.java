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
package org.doodle.design.broker.frame;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.metadata.WellKnownMimeType;
import java.util.Map;
import java.util.function.Function;
import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeType;

public class BrokerFrameExtractor implements Function<Payload, BrokerFrame> {
  private final Function<Payload, BrokerFrame> frameExtractor;

  public BrokerFrameExtractor(RSocketStrategies strategies) {
    this.frameExtractor =
        (payload) -> {
          MimeType mimeType =
              MimeType.valueOf(
                  (payload instanceof ConnectionSetupPayload setupPayload)
                      ? setupPayload.metadataMimeType()
                      : WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.toString());
          MetadataExtractor metadataExtractor = strategies.metadataExtractor();
          Map<String, Object> payloadMetadata = metadataExtractor.extract(payload, mimeType);
          if (payloadMetadata.containsKey(BrokerFrameMimeTypes.BROKER_FRAME_METADATA_KEY)) {
            return (BrokerFrame)
                payloadMetadata.get(BrokerFrameMimeTypes.BROKER_FRAME_METADATA_KEY);
          }
          return null;
        };
  }

  @Override
  public BrokerFrame apply(Payload payload) {
    return this.frameExtractor.apply(payload);
  }
}
