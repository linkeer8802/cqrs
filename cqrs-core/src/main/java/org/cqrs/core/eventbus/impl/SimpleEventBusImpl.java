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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.cqrs.core.eventbus.Message;
import org.cqrs.core.eventbus.MessageHandler;

/**
 * @author weird
 */
public class SimpleEventBusImpl extends AbstractEventBus {

  @Override
  public void delivery(String address, Message<?> event) {
    dispatch(address, event);
  }

  @Override
  public <T> T execute(String address, Object command) {
    
    CompletableFuture<T> future = new CompletableFuture<T>();
    
    send(address, command, new MessageHandler<T>() {
      @Override
      public void handle(Message<T> message) {
        future.complete(message.getBody());
      }
    });
    
    try {
      return future.get(5L, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new IllegalStateException(e);
    }
  }
}
