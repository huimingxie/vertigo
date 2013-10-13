/*
* Copyright 2013 the original author or authors.
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
package net.kuujo.vertigo.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.kuujo.vertigo.Component;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * A JSON object-based component context.
 *
 * @author Jordan Halterman
 */
public class ComponentContext implements Context {

  private JsonObject context = new JsonObject();

  private NetworkContext parent;

  public ComponentContext() {
  }

  public ComponentContext(String name) {
    context.putString("name", name);
  }

  private ComponentContext(JsonObject json) {
    context = json;
    JsonObject networkContext = context.getObject("network");
    if (networkContext != null) {
      parent = NetworkContext.fromJson(networkContext);
    }
  }

  private ComponentContext(JsonObject json, NetworkContext parent) {
    this(json);
    this.parent = parent;
  }

  /**
   * Creates a component context from JSON.
   *
   * @param json
   *   A JSON representation of the component context.
   * @return
   *   A new component context instance.
   */
  public static ComponentContext fromJson(JsonObject json) {
    return new ComponentContext(json);
  }

  /**
   * Creates a component context from JSON.
   *
   * @param json
   *   A JSON representation of the component context.
   * @param parent
   *   The parent network context.
   * @return
   *   A new component context instance.
   */
  public static ComponentContext fromJson(JsonObject json, NetworkContext parent) {
    return new ComponentContext(json, parent);
  }

  /**
   * Returns the component name.
   *
   * @return
   *   The component name.
   */
  public String name() {
    return getDefinition().name();
  }

  /**
   * Returns the component address.
   *
   * @return
   *   The component address.
   */
  public String address() {
    return context.getString("address");
  }

  /**
   * Returns a list of component connections.
   *
   * @return
   *   A collection of component connections.
   */
  public Collection<ConnectionContext> getConnectionContexts() {
    Set<ConnectionContext> contexts = new HashSet<ConnectionContext>();
    JsonObject connections = context.getObject("connections");
    Iterator<String> iter = connections.getFieldNames().iterator();
    while (iter.hasNext()) {
      contexts.add(new ConnectionContext(connections.getObject(iter.next())));
    }
    return contexts;
  }

  /**
   * Returns a component connection context.
   *
   * @param name
   *   The name of the component whose connection to return.
   * @return
   *   A component connection context.
   */
  public ConnectionContext getConnectionContext(String name) {
    JsonObject connections = context.getObject("connections");
    if (connections == null) {
      connections = new JsonObject();
    }
    JsonObject connectionContext = connections.getObject(name);
    return connectionContext != null ? new ConnectionContext(connectionContext, this) : null;
  }

  /**
   * Returns all worker contexts.
   *
   * @return
   *   A collection of worker contexts.
   */
  public Collection<WorkerContext> getWorkerContexts() {
    JsonArray workers = context.getArray("workers");
    ArrayList<WorkerContext> contexts = new ArrayList<WorkerContext>();
    Iterator<Object> iter = workers.iterator();
    while (iter.hasNext()) {
      contexts.add(new WorkerContext(context.copy().putString("address", (String) iter.next()), this));
    }
    return contexts;
  }

  /**
   * Returns the component definition.
   *
   * @return
   *   The component definition.
   */
  public Component getDefinition() {
    JsonObject definition = context.getObject("definition");
    return definition != null ? Component.fromJson(definition) : null;
  }

  /**
   * Returns the parent network context.
   *
   * @return
   *   The component's parent network context.
   */
  public NetworkContext getNetworkContext() {
    return parent;
  }

  @Override
  public JsonObject serialize() {
    JsonObject context = this.context.copy();
    if (parent != null) {
      context.putObject("network", parent.serialize());
    }
    return context;
  }

}
