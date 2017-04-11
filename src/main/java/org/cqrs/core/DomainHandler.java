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

/**
 * @author weird
 */
public abstract class DomainHandler<T extends EventSourcingAggregateRoot> {

  public DomainHandler(Class<T> domainType) {
    handle(CQRS.get().domain(domainType));
  }
  
  public abstract void handle(DomainContext<T> domainContext);
  
//  public static <R extends EventSourcingAggregateRoot>  DomainHandler<R> register(Class<R> domainType) {
//    
//  }
}
