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

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.cqrs.core.eventbus.Message;
import org.cqrs.core.eventbus.MessageHandler;

/**
 * @author weird
 */
public class MessageHandlerRegistry<T> {

  private List<MessageHandler<T>> handlers;
  
  public MessageHandlerRegistry() {
    this.handlers = new CopyOnWriteArrayList<>();
  }
  
  public boolean addHandler(MessageHandler<T> handler) {
    return this.handlers.add(handler);
  }
  
  public boolean removeHandler(MessageHandler<T> handler) {
    return this.handlers.remove(handler);
  }
  
  public void handle(Message<T> message) {
    
    if (message.isSend()) {
      
      handlers.get(new Random().nextInt(handlers.size())).handle(message);
      
    } else {
      
      for (MessageHandler<T> messageHandler : handlers) {
        messageHandler.handle(message);
      }
    }
  }
}
