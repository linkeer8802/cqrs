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

package org.cqrs.core;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.cqrs.util.SequenceUUID;

/**
 * @author weird
 */
public class CommandContext {
  
  private Object command;
  
  public CommandContext(Object command) {
    super();
    this.command = command;
  }
  
  public String uniqueId() {
    return SequenceUUID.get().toString();
  }
  
  public void publishEvent(EventSourcingAggregateRoot aggregateRoot, DomainEvent event) {
    if (aggregateRoot == null) {
      CQRS.get().applyEvent(null, event);
    } else {
      aggregateRoot.apply(event);
    }
  }
  
  public void send(Object name, Object command) {
    CQRS.get().send(name, command);
  }
  
  @SuppressWarnings("unchecked")
  public <C> C command(Class<C> type) {
    return (C) command;
  }
  
  @SuppressWarnings("unchecked")
  private Object map(String name) {
    if (command instanceof Map) {
      return ((Map<String, Object>) command).get(name);
    } else {
      return null;
    }
  }
  
  public Object arg(String name) {
    return map(name);
  }
  
  public String strArg(String name) {
    return (String) map(name);
  }
  
  public Integer intArg(String name) {
    return (Integer) map(name);
  }
  
  public Double doubleArg(String name) {
    return (Double) map(name);
  }
  
  public Boolean boolArg(String name) {
    return (Boolean) map(name);
  }
  
  public BigDecimal decimalArg(String name) {
    return (BigDecimal) map(name);
  }

  public Date dateArg(String name) {
    return (Date) map(name);
  }
}
