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

public abstract class EventSourcingAggregateRoot implements AggregateRoot {

  protected String id;
  Boolean isReplay;
  Long version;

  public EventSourcingAggregateRoot(String id) {
    this.id = id;
    version = 0L;
    isReplay = false;
  }

  public Long getVersion() {
    return version;
  }
  
  public String getId() {
    return id;
  }
  
  public void setIsReplay(Boolean isReplay) {
    this.isReplay = isReplay;
  }
  
  public Boolean getIsReplay() {
    return isReplay;
  }
  
  protected final EventSourcingAggregateRoot incrVersion(EventSourcingAggregateRoot prev) {
    this.version = (prev == null ? this.version : prev.version) + 1;
    return this;
  }

  protected void apply(DomainEvent event) {
    if (this.id == null) {
      throw new IllegalStateException("The id attr is null.");
    }
    CQRS.get().applyEvent(this, event);
  }
}
