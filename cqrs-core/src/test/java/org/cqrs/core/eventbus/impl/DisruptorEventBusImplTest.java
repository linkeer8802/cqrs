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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.cqrs.core.eventbus.EventBus;
import org.cqrs.core.eventbus.ReplyableMessage;
import org.cqrs.core.eventbus.impl.DisruptorEventBusImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DisruptorEventBusImplTest {

  private EventBus eventBus;
  
  @Before
  public void setUp() throws Exception {
    eventBus = DisruptorEventBusImpl.startNew(Executors.newCachedThreadPool(), 2048);
  }

  @After
  public void tearDown() throws Exception {
    ((DisruptorEventBusImpl)eventBus).shutdown();
  }
  
  public static void main(String[] args) throws Exception {
    
    EventBus eventBus = DisruptorEventBusImpl.startNew(Executors.newCachedThreadPool(), 2048);
//    EventBus eventBus = new SimpleEventBusImpl();
    
    String address = "test";
    
//    eventBus.subscribe(address, message -> {System.out.println(message.getBody());});
//    eventBus.send(address, "aaaaaaaaaaaaaaa");
    
    eventBus.subscribe(address, message -> {
//      throw new RuntimeException("error~~~~");
      System.out.println(message.getBody());
      ((ReplyableMessage<?>)message).reply("ccccccf");
    });
//    String r = eventBus.send(address, "bbbb", new CompletableFuture<String>()).get();
    
    String r = eventBus.execute(address, "aaaa");
    System.out.println(r);
    
    ((DisruptorEventBusImpl)eventBus).shutdown();
  }

  @Test
  public void testPublish() throws Exception {
    
    String address = "test";
    Integer value = 3;
    CompletableFuture<Integer> future = new CompletableFuture<Integer>();
    
    eventBus.subscribe(address , message -> {
      future.complete((int) message.getBody());
    });
    
    eventBus.publish(address, value);
    Assert.assertEquals(value, future.get(5L, TimeUnit.SECONDS));
  }
  
  @Test(expected=TimeoutException.class)
  public void testPublishNoSubscribe() throws Exception {
    
    String address = "test";
    Integer value = 3;
    CompletableFuture<Integer> future = new CompletableFuture<Integer>();
    
    eventBus.publish(address, value);
    future.get(5L, TimeUnit.SECONDS);
  }
  
  @Test
  public void testExecute() {
    
    String address = "test";
    AtomicInteger value = new AtomicInteger(0);
    int incrValue = 3;
    
    eventBus.subscribe(address , message -> {
      value.set(value.get() + (int) message.getBody());
      ((ReplyableMessage<?>)message).reply(message.getBody());
    });
    
    Object result = eventBus.execute(address, incrValue);
    Assert.assertEquals(incrValue, result);
    
    Assert.assertEquals(incrValue, value.get());
  }
}
