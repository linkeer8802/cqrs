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

import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.NavigableMap;

import org.cqrs.core.DomainEvent;
import org.cqrs.core.EventSourcingAggregateRoot;
import org.cqrs.core.eventbus.OnEvent;
import org.cqrs.core.journals.JournalsStorage;
import org.cqrs.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSourcingRepository extends AbstractEventRepository {

  static final Logger logger = LoggerFactory.getLogger(EventSourcingRepository.class);

  public EventSourcingRepository(EventStorage eventStorage, JournalsStorage journalsStorage) {
    super(eventStorage, journalsStorage);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected <T extends EventSourcingAggregateRoot> T replayEventsToAggregateRoot(T root, NavigableMap<Long, DomainEvent> events) {
    for (Entry<Long, DomainEvent> entity : events.entrySet()) {
      long _version = entity.getKey();
      DomainEvent event = entity.getValue();
      
      // replay event
      for (Method method : root.getClass().getDeclaredMethods()) {
        if (method.isAnnotationPresent(OnEvent.class) 
            && method.getParameterCount() == 1
            && method.getParameterTypes()[0].equals(event.getClass())) {

          try {
            method.setAccessible(true);
            Object target = method.invoke(root, event);
            if (target == null || !root.getClass().isInstance(target)) {
              throw new RuntimeException("Method must return the [" + root.getClass() + "] instance.");
            }
            // set aggregateRoot id and version attrs.
            Objects.setField("id", root.getId(), target);
            Objects.setField("version", _version, target);
            root = (T) target;
          } catch (Exception e) {
            throw new IllegalStateException("AggregateRoot replay fail.", e);
          }
        }
      }
    }

    return root;
  }
}
