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
package io.rsocket.core;

import io.netty.buffer.ByteBuf;
import io.rsocket.DuplexConnection;
import org.doodle.design.socket.SocketConnection;
import reactor.core.publisher.MonoSink;
import reactor.netty.Connection;
import reactor.util.function.Tuple2;

public class SocketServerSetupHandlingConnection extends SetupHandlingDuplexConnection
    implements SocketConnection {

  public SocketServerSetupHandlingConnection(
      SocketConnection source, MonoSink<Tuple2<ByteBuf, DuplexConnection>> sink) {
    super(source, sink);
  }

  @Override
  public Connection connection() {
    return ((SocketConnection) source).connection();
  }
}
