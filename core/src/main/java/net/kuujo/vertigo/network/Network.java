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
package net.kuujo.vertigo.network;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import net.kuujo.vertigo.component.Component;
import net.kuujo.vertigo.component.ComponentOptions;
import net.kuujo.vertigo.io.connection.ConnectionOptions;
import net.kuujo.vertigo.io.connection.SourceOptions;
import net.kuujo.vertigo.io.connection.TargetOptions;

import java.io.Serializable;
import java.util.Collection;

/**
 * Network configuration.<p>
 *
 * The network configuration defines a collection of components
 * (Vert.x modules and verticles) that can be connected together in
 * a meaningful way.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@VertxGen
public interface Network extends Serializable {

  /**
   * <code>name</code> is a string indicating the unique network name. This is the
   * address at which the network will monitor network components. This field is required.
   */
  public static final String NETWORK_NAME = "name";

  /**
   * <code>components</code> is an object defining network component configurations. Each
   * item in the object must be keyed by the unique component address, with each item
   * being an object containing the component configuration. See the {@link net.kuujo.vertigo.component.ComponentOptions}
   * class for component configuration options.
   */
  public static final String NETWORK_COMPONENTS = "components";

  /**
   * <code>connections</code> is an array defining network connection configurations. Each
   * item in the array must be an object defining a <code>source</code> and <code>target</code>
   * configuration.
   */
  public static final String NETWORK_CONNECTIONS = "connections";

  /**
   * Returns the network name.<p>
   *
   * The network's name should be unique within a given cluster.
   * 
   * @return The network name.
   */
  String getName();

  /**
   * Gets a list of network components.
   * 
   * @return A list of network components.
   */
  Collection<ComponentOptions> getComponents();

  /**
   * Gets a component by name.
   * 
   * @param name The component name.
   * @return The component configuration.
   * @throws IllegalArgumentException If the given component address does not exist within
   *           the network.
   */
  ComponentOptions getComponent(String name);

  /**
   * Returns a boolean indicating whether the network has a component.
   *
   * @param name The component name.
   * @return Indicates whether the component exists in the network.
   */
  boolean hasComponent(String name);

  /**
   * Adds a component to the network.
   *
   * @param component The component verticle.
   * @return The network definition.
   */
  @GenIgnore
  Network addComponent(Component component);

  /**
   * Adds a component to the network.
   *
   * @param component The component verticle.
   * @param options The component options.
   * @return The network definition.
   */
  @GenIgnore
  Network addComponent(Component component, ComponentOptions options);

  /**
   * Adds a component to the network.
   *
   * @param name The component name.
   * @param component The component verticle.
   * @return The network definition.
   */
  @GenIgnore
  Network addComponent(String name, Component component);

  /**
   * Adds a component to the network.
   *
   * @param name The component name.
   * @param component The component verticle.
   * @param options The component options.
   * @return The network definition.
   */
  @GenIgnore
  Network addComponent(String name, Component component, ComponentOptions options);

  /**
   * Adds a component to the network.
   *
   * @param main The component verticle main.
   * @return The network definition.
   */
  @Fluent
  Network addComponent(String main);

  /**
   * Adds a component to the network.
   *
   * @param main The component verticle main.
   * @param options The component options.
   * @return The network definition.
   */
  @Fluent
  Network addComponent(String main, ComponentOptions options);

  /**
   * Adds a component to the network.
   *
   * @param name The component name.
   * @param main The component verticle main.
   * @return The network definition.
   */
  @Fluent
  Network addComponent(String name, String main);

  /**
   * Adds a component to the network.
   *
   * @param name The component name.
   * @param main The component verticle main.
   * @param options The component options.
   * @return The network definition.
   */
  @Fluent
  Network addComponent(String name, String main, ComponentOptions options);

  /**
   * Removes a component from the network.
   *
   * @param name The component name.
   * @return The network definition.
   */
  @Fluent
  Network removeComponent(String name);

  /**
   * Returns a collection of network connections.
   *
   * @return A collection of connections in the network.
   */
  Collection<ConnectionOptions> getConnections();

  /**
   * Creates a connection between two components.
   *
   * @param connection The new connection options.
   * @return The network definition.
   */
  @Fluent
  Network createConnection(ConnectionOptions connection);

  /**
   * Creates a connection between two components.
   *
   * @param source The source connection options.
   * @param target The target connection options.
   * @return The network definition.
   */
  @Fluent
  Network createConnection(SourceOptions source, TargetOptions target);

  /**
   * Destroys a connection between two components.
   *
   * @param connection The connection to destroy.
   * @return The network definition.
   */
  @Fluent
  Network destroyConnection(ConnectionOptions connection);

}
