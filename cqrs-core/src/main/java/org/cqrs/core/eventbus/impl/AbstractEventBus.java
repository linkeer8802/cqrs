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
import java.util.concurrent.CompletableFuture;
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
  
  public void publish(String address, Object message) {
    
    logger.debug("Publish message at address={}", address);
    
    deliver(address, buildMessage(address, message, false, false));
  }
  
  @Override
  public void send(String address, Object message) {
    
    logger.debug("Send message at address={}", address);
    
    deliver(address, buildMessage(address, message, false, true));
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> CompletableFuture<T> send(String address, Object message, final CompletableFuture<T> future) {
    
    logger.debug("Send future message at address={}", address);
    
    ReplyableMessage<?> messageInfo = (ReplyableMessage<?>) buildMessage(address, message, true, true);
    subscribeOnce(messageInfo.getReplyAddress(), (replyMessage) -> {
      
      if (replyMessage.getBody() instanceof Throwable) {
        
        future.completeExceptionally((Throwable)replyMessage.getBody());
        
      } else {
        future.complete((T) replyMessage.getBody());
      }
    });
    
    deliver(address, messageInfo);
    
    return future;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Message<?> buildMessage(String address, Object message, Boolean isReply, boolean send) {
    if (isReply) {
      return new ReplyableMessage(this, message.getClass(), message, send).addHeader(Message.HEADER_MESSAGE_ADDR, address);
    } else {
      return new Message(message.getClass(), message, send).addHeader(Message.HEADER_MESSAGE_ADDR, address);
    }
  }
  
  public abstract void deliver(String address, Message<?> message);
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void dispatch(String address, Message message) {
    
    MessageHandlerRegistry<?> registry = registryMap.get(address);
    
    interceptors.stream().forEach(interceptor -> interceptor.beforeHandle(message));
    
      if (registry != null) {
        try {
            registry.handle(message);
            
            interceptors.stream().forEach(interceptor -> interceptor.onSuccess(message));
            
          } catch (Exception e) {
            
            if (message instanceof ReplyableMessage) {
              ((ReplyableMessage)message).fail(e);
            }
            
            interceptors.stream().forEach(interceptor -> interceptor.onError(message, e));
          }
        
      } else {
        logger.warn("No registry for message address={}", message.getAddress());
      }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void subscribe(String address, MessageHandler handler) {
    if (!registryMap.containsKey(address)) {
      registryMap.put(address, new MessageHandlerRegistry());
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
