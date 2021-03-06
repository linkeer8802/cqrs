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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author weird
 */
public class SagaContext {

  protected Map<String, Object> attrs;
  
  public SagaContext() {
    this.attrs = new HashMap<>();
  }
  
  public SagaContext associateWith(String property, Object value) {
    attrs.put(property, value);
    return this;
  }
  
  @SuppressWarnings("unchecked")
  public <T> T associationProperty(String property) {
    return (T) attrs.get(property);
  }

  public <T> CompletableFuture<T> send(Object name, Object command) {
    return CQRS.get().send(name, command);
  }

  public void end() {
    
  }
}
