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

import io.netty.buffer.ByteBuf;
import io.rsocket.DuplexConnection;
import io.rsocket.core.SocketServerSetupHandlingConnection;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;
import java.util.function.BiFunction;
import org.doodle.design.socket.keepalive.KeepAliveHandler;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public final class SocketServerSetup {
  final Duration timeout;

  public SocketServerSetup(Duration timeout) {
    this.timeout = timeout;
  }

  Mono<Tuple2<ByteBuf, DuplexConnection>> init(SocketConnection connection) {
    return Mono.<Tuple2<ByteBuf, DuplexConnection>>create(
            sink -> sink.onRequest(__ -> new SocketServerSetupHandlingConnection(connection, sink)))
        .timeout(this.timeout)
        .or(connection.onClose().then(Mono.error(ClosedChannelException::new)));
  }

  public Mono<Void> acceptSetup(
      ByteBuf startFrame,
      SocketConnection connection,
      BiFunction<KeepAliveHandler, SocketConnection, Mono<Void>> then) {
    SocketConnectionSetupPayload setupPayload =
        new SocketConnectionSetupPayload(startFrame.retain());
    return then.apply(new KeepAliveHandler(connection), connection);
  }
}
