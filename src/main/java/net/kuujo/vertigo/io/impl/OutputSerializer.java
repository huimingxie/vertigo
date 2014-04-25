/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kuujo.vertigo.io.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.kuujo.vertigo.util.serialization.Serializer;
import net.kuujo.vertigo.util.serialization.SerializerFactory;

import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * Handles serialization of output messages.<p>
 *
 * The serializer serializes all messages to a {@link JsonObject} which
 * can be read and deserialized by input connections. If the message is
 * not an event bus supported type, the serializer will attempt to
 * serialize the message using the default Vertigo serializer.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class OutputSerializer {
  private final Map<Class<?>, Serializer> serializers = new HashMap<>();
  @SuppressWarnings("serial")
  private final Set<Class<?>> eventBusTypes = new HashSet<Class<?>>() {{
    add(String.class);
    add(Integer.class);
    add(Short.class);
    add(Integer.class);
    add(Long.class);
    add(Float.class);
    add(Double.class);
    add(Byte.class);
    add(byte[].class);
    add(Character.class);
    add(Buffer.class);
    add(JsonObject.class);
    add(JsonArray.class);
  }};

  /**
   * Serializes a message.
   *
   * @param message The message to serialzie.
   * @return The serialized message.
   */
  public JsonObject serialize(Object message) {
    Class<?> clazz = message.getClass();
    if (eventBusTypes.contains(clazz)) {
      return new JsonObject().putValue("value", message);
    }
    Serializer serializer = serializers.get(clazz);
    if (serializer == null) {
      serializer = SerializerFactory.getSerializer(clazz);
      serializers.put(clazz, serializer);
    }
    return new JsonObject().putBoolean("serialized", true).putString("class", clazz.getName()).putString("value", serializer.serializeToString(message));
  }

}