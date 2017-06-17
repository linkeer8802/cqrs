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

import java.util.concurrent.atomic.AtomicInteger;

import org.cqrs.core.eventbus.EventBus;
import org.cqrs.core.eventbus.ReplyableMessage;
import org.cqrs.core.eventbus.impl.SimpleEventBusImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class SimpleEventBusImplTest {

  private EventBus eventBus;
  
  @Before
  public void setUp() throws Exception {
    eventBus = new SimpleEventBusImpl();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testAll() {
    
    String address = "test";
    AtomicInteger value = new AtomicInteger(0);
    AtomicInteger value2 = new AtomicInteger(0);
    int incrValue = 3;
    
    eventBus.subscribe(address , message -> {
      value.set(value.get() + (int) message.getBody());
    });
    
    eventBus.publish(address, incrValue);
    Assert.assertEquals(incrValue, value.get());
    
    eventBus.send(address, incrValue);
    Assert.assertEquals(incrValue*2, value.get());
    
    eventBus.subscribeOnce(address , message -> {
      value2.set(value2.get() + (int) message.getBody());
    });
    
    eventBus.publish(address, incrValue);
    eventBus.publish(address, incrValue);
    eventBus.publish(address, incrValue);
    Assert.assertEquals(incrValue, value2.get());
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
