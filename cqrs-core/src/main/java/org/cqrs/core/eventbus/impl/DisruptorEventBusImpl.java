/** 
 *  Copyright (c) 2017 The original author or authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.cqrs.core.eventbus.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.cqrs.core.Component;
import org.cqrs.core.eventbus.EventBus;
import org.cqrs.core.eventbus.Message;
import org.cqrs.core.eventbus.ReplyableMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * @author weird
 */
public class DisruptorEventBusImpl extends AbstractEventBus implements Component {

  static final Logger logger = LoggerFactory.getLogger(DisruptorEventBusImpl.class);
  
  private int bufferSize;
  private ExecutorService executor;
  private Disruptor<DisruptorEvent> disruptor;
  
  public DisruptorEventBusImpl(ExecutorService executor, int bufferSize) {
    super();
    this.bufferSize = bufferSize;
    this.executor = executor;
  }
  
  public static EventBus startNew(ExecutorService executor, int bufferSize) {
    DisruptorEventBusImpl eventBus = new DisruptorEventBusImpl(executor, bufferSize);
    eventBus.start();
    return eventBus;
  }
  
  @SuppressWarnings({ "unchecked", "deprecation" })
  public void start() {
    disruptor = new Disruptor<>(DisruptorEvent::new, bufferSize, executor);
    disruptor.handleEventsWith(new DisruptorEventhandler(this));
    disruptor.setDefaultExceptionHandler(new IgnoreExceptionHandler());
    disruptor.start();
  }
  
  public void shutdown() {
    executor.shutdown();
    disruptor.shutdown();
  }

  @Override
  public void deliver(String address, Message<?> message) {
    disruptor.getRingBuffer().publishEvent((event, sequence) -> {
      event.setMessage(message);
    });
  }
  
  public <T> T execute(String address, Object message) {
    try {
      return execute(address, message, 5L);
    } catch (TimeoutException e) {
      throw new IllegalStateException(e);
//      logger.error("execute error.", e);
//      return null;
    }
  }

  @SuppressWarnings({ "unchecked"})
  public <T> T execute(String address, Object message, Long timeoutInSeconds) throws TimeoutException {
    
    logger.debug("Execute message at address={}", address);
    
    CompletableFuture<T> future = new CompletableFuture<T>();
    ReplyableMessage<?> messageInfo = (ReplyableMessage<?>) buildMessage(address, message, true, true);
    
    subscribeOnce(messageInfo.getReplyAddress(), (replyMessage) -> {
      
      future.complete((T) replyMessage.getBody());
    });
    
    deliver(address, messageInfo);

    T result = null;
    try {
      if (timeoutInSeconds != null) {
        result = future.get(timeoutInSeconds, TimeUnit.SECONDS);
      } else {
        result = future.get();
      }
      
      if (result instanceof Throwable) {
        throw new IllegalStateException((Throwable)result);
      } else {
        return result;
      }
    } catch (InterruptedException | ExecutionException e) {
      throw new IllegalStateException(e);
    }
  }
  
  class DisruptorEventhandler implements EventHandler<DisruptorEvent> {

    private AbstractEventBus eventbus;
    
    public DisruptorEventhandler(AbstractEventBus eventbus) {
      this.eventbus = eventbus;
    }
    
    @Override
    public void onEvent(DisruptorEvent event, long sequence, boolean endOfBatch) throws Exception {
      
      String address = (String)event.getMessage().getAddress();
      
      logger.debug("consumer message address={}", address);
      
      eventbus.dispatch(address, event.message);
    }
  }
  
  static class DisruptorEvent {
    
    private Message<?> message;
    
    public Message<?> getMessage() {
      return message;
    }
    
    public void setMessage(Message<?> message) {
      this.message = message;
    }
  }
}
