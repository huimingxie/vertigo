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
package net.kuujo.vertigo.feeder;

import net.kuujo.vertigo.context.InstanceContext;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.impl.DefaultFutureResult;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

/**
 * A default polling feeder implementation.
 *
 * @author Jordan Halterman
 */
public class DefaultPollingFeeder extends AbstractFeeder<PollingFeeder> implements PollingFeeder {
  private Handler<PollingFeeder> feedHandler;
  private Handler<String> ackHandler;
  private Handler<String> failHandler;
  private static final long DEFAULT_FEED_DELAY = 10;
  private long feedDelay = DEFAULT_FEED_DELAY;
  private boolean fed;

  private Handler<String> internalAckHandler = new Handler<String>() {
    @Override
    public void handle(String messageId) {
      if (ackHandler != null) {
        ackHandler.handle(messageId);
      }
    }
  };

  private Handler<String> internalFailHandler = new Handler<String>() {
    @Override
    public void handle(String messageId) {
      if (failHandler != null) {
        failHandler.handle(messageId);
      }
    }
  };

  public DefaultPollingFeeder(Vertx vertx, Container container, InstanceContext context) {
    super(vertx, container, context);
  }

  @Override
  public PollingFeeder setFeedDelay(long delay) {
    feedDelay = delay;
    return this;
  }

  @Override
  public long getFeedDelay() {
    return feedDelay;
  }

  @Override
  public PollingFeeder feedHandler(Handler<PollingFeeder> handler) {
    feedHandler = handler;
    return this;
  }

  @Override
  public PollingFeeder start() {
    return super.start(new Handler<AsyncResult<PollingFeeder>>() {
      @Override
      public void handle(AsyncResult<PollingFeeder> result) {
        if (result.succeeded()) {
          recursiveFeed();
        }
      }
    });
  }

  @Override
  public PollingFeeder start(Handler<AsyncResult<PollingFeeder>> doneHandler) {
    final Future<PollingFeeder> future = new DefaultFutureResult<PollingFeeder>().setHandler(doneHandler);
    return super.start(new Handler<AsyncResult<PollingFeeder>>() {
      @Override
      public void handle(AsyncResult<PollingFeeder> result) {
        if (result.failed()) {
          future.setFailure(result.cause());
        }
        else {
          recursiveFeed();
          future.setResult(result.result());
        }
      }
    });
  }

  /**
   * Recursively invokes the feed handler.
   * If the feed handler is invoked and no messages are fed from the handler,
   * a timer is set to restart the feed in the future.
   */
  private void recursiveFeed() {
    fed = true;
    while (fed && !queueFull()) {
      fed = false;
      doFeed();
    }

    vertx.setTimer(feedDelay, new Handler<Long>() {
      @Override
      public void handle(Long timerID) {
        recursiveFeed();
      }
    });
  }

  /**
   * Invokes the feed handler.
   */
  private void doFeed() {
    if (feedHandler != null) {
      feedHandler.handle(this);
    }
  }

  @Override
  public PollingFeeder ackHandler(Handler<String> ackHandler) {
    this.ackHandler = ackHandler;
    return this;
  }

  @Override
  public PollingFeeder failHandler(Handler<String> failHandler) {
    this.failHandler = failHandler;
    return this;
  }

  @Override
  public String emit(JsonObject data) {
    fed = true;
    return doFeed(data, null, 0, internalAckHandler, internalFailHandler);
  }

  @Override
  public String emit(JsonObject data, String tag) {
    fed = true;
    return doFeed(data, tag, 0, internalAckHandler, internalFailHandler);
  }

}
