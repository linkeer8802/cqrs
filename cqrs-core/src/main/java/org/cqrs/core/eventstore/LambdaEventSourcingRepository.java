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

import java.util.Map.Entry;
import java.util.NavigableMap;

import org.cqrs.core.CQRS;
import org.cqrs.core.DomainEvent;
import org.cqrs.core.EventSourcingAggregateRoot;
import org.cqrs.core.journals.JournalsStorage;

/**
 * @author weird
 */
public class LambdaEventSourcingRepository extends AbstractEventRepository {

  public LambdaEventSourcingRepository(EventStorage<?> eventStorage, JournalsStorage journalsStorage) {
    super(eventStorage, journalsStorage);
  }

  @Override
  protected <T extends EventSourcingAggregateRoot> T replayEventsToAggregateRoot(T root, NavigableMap<Long, DomainEvent> events) {
   
    for (Entry<Long, DomainEvent> entity : events.entrySet()) {

      try {
        root = (T) CQRS.get().replayEvent(root, entity.getValue());
        root.setIsReplay(true);
      } catch (Exception e) {
        throw new IllegalStateException("AggregateRoot replay fail.", e);
      }
    }

    return root;
  }
}
