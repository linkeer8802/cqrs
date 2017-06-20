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

package org.cqrs.core.eventstore;

import java.util.NavigableMap;
import java.util.Map.Entry;

import org.cqrs.core.CQRS;
import org.cqrs.core.DomainEvent;
import org.cqrs.core.EventSourcingAggregateRoot;
import org.cqrs.core.journals.JournalsStorage;
import org.cqrs.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * @author weird
 */
public abstract class AbstractEventRepository implements EventRepository{
  
  static final Logger logger = LoggerFactory.getLogger(AbstractEventRepository.class);
  
  protected EventStorage<?> eventStorage;
  protected JournalsStorage journalsStorage;
  
  public AbstractEventRepository(EventStorage<?> eventStorage, JournalsStorage journalsStorage) {
    this.eventStorage = eventStorage;
    this.journalsStorage = journalsStorage;
  }
  
  @Override
  public <T extends EventSourcingAggregateRoot> T get(Class<T> type, String aggregateId) {
    
    T root = eventStorage.getAggregateRootFromSnapshot(aggregateId);
    if (root == null) {
      root = Objects.init(type, ImmutableMap.of("version", 0L, "id", aggregateId));
    }
    root.setIsReplay(true);
    
    NavigableMap<Long, DomainEvent> events = eventStorage.readEvents(aggregateId, root.getVersion());

    if (events == null || events.size() == 0) {
      return root;
    }
    
    //重新发布未完成的事件
    Long lastVersion = journalsStorage.getLastVersion(aggregateId);
    if (lastVersion != null && lastVersion < events.lastKey()) {
      
      logger.debug("Publish unsubmit event from version {} to {}", lastVersion, events.lastKey());
      
      for (Entry<Long, DomainEvent> entity : events.tailMap(lastVersion, false).entrySet()) {
        CQRS.get().publishEvent(entity.getValue().getClass().getName(), entity.getValue());
      }
    }
    
    return replayEventsToAggregateRoot(root, events);
  }
  
  protected abstract <T extends EventSourcingAggregateRoot> T replayEventsToAggregateRoot(T root, NavigableMap<Long, DomainEvent> events);

  @Override
  public void saveEvent(DomainEvent event) {
    eventStorage.appendEvent(event);
  }
}
