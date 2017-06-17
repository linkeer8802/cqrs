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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.cqrs.core.eventbus.EventBus;
import org.cqrs.core.eventbus.Message;
import org.cqrs.core.eventbus.MessageHandler;
import org.cqrs.core.eventbus.MessageInterceptor;
import org.cqrs.core.eventbus.ReplyableMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author weird
 */
public abstract class AbstractEventBus implements EventBus {
  
  static final Logger logger = LoggerFactory.getLogger(AbstractEventBus.class);

  protected Map<String, MessageHandlerRegistry<?>> registryMap;
  
  private List<MessageInterceptor> interceptors;
  
  
  public AbstractEventBus() {
    registryMap = new ConcurrentHashMap<>();
    interceptors = new ArrayList<>();
    addMessageInterceptor(new DefaultMessageInterceptor());
  }
  
  @Override
  public EventBus addMessageInterceptor(MessageInterceptor interceptor) {
    interceptors.add(interceptor);
    return this;
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void publish(String address, Object message) {
    
    logger.debug("Publish message at address={}", address);
    
    delivery(address, new Message(message.getClass(), message).addHeader(Message.HEADER_MESSAGE_ADDR, address));
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void send(String address, Object message) {
    
    logger.debug("Send message at address={}", address);
    
    delivery(address, new ReplyableMessage(this, message.getClass(), message, false).addHeader(Message.HEADER_MESSAGE_ADDR, address));
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void send(String address, Object message, MessageHandler<?> replyhandler) {
    
    subscribe(address + ReplyableMessage.HEADER_MESSAGE_RSP, (replyMessage) -> {
      
      replyhandler.handle(replyMessage);
      unsubscribe(address + ReplyableMessage.HEADER_MESSAGE_RSP);
      
    });
    
    send(address, message);
  }
  
  public abstract void delivery(String address, Message<?> message);
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void dispatch(String address, Message message) {
    
    MessageHandlerRegistry<?> registry = registryMap.get(address);
    
    interceptors.stream().forEach(interceptor -> interceptor.beforeHandle(message));
    
      if (registry != null) {
        try {
            registry.handle(message);
          } catch (Exception e) {
            interceptors.stream().forEach(interceptor -> interceptor.onError(message, e));
          }
        
        interceptors.stream().forEach(interceptor -> interceptor.onSuccess(message));
      }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void subscribe(String address, MessageHandler handler) {
    if (!registryMap.containsKey(address)) {
      registryMap.put(address, new MessageHandlerRegistry(address));
    }
    registryMap.get(address).addHandler(handler);
  }
  
  @SuppressWarnings({ "rawtypes" })
  @Override
  public void subscribeOnce(String address, MessageHandler handler) {
    
    MessageHandler _handler = new MessageHandler() {
      @SuppressWarnings("unchecked")
      @Override
      public void handle(Message message) {
        handler.handle(message);
        unsubscribe(address, this);
      }
    };
    subscribe(address, _handler);
  }
  
  @Override
  public void unsubscribe(String address) {
    registryMap.remove(address);
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void unsubscribe(String address, MessageHandler handler) {
    registryMap.get(address).removeHandler(handler);
  }
}
