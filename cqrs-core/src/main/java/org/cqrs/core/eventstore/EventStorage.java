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

import org.cqrs.core.DomainEvent;
import org.cqrs.core.EventSourcingAggregateRoot;

public abstract class EventStorage<U> {

	protected Serializer<DomainEvent, U> eventSerializer;
	protected Serializer<? super EventSourcingAggregateRoot, U> snapshotSerializer;
	
//	protected String getAggregateId(EventSourcingAggregateRoot root, DomainEvent event) {
//	  
//		if (root.getId() != null) {
//			return root.getId();
//		} else if (event instanceof DomainCreatedEvent) {
//			return DomainCreatedEvent.class.cast(event).getAggregateId();
//		}
//		throw new RuntimeException("AggregateId not found.");
//	}
	
	abstract void appendEvent(DomainEvent event);
	
	abstract NavigableMap<Long, DomainEvent> readEvents(String aggregateId, long fromVersion);
	
	abstract <T extends EventSourcingAggregateRoot> void storeSnapshot(T root);
	
	abstract void deleteSnapshots(String aggregateId);
	
	abstract <T extends EventSourcingAggregateRoot> T getAggregateRootFromSnapshot(String aggregateId);
	
	public void setEventSerializer(Serializer<DomainEvent, U> serializer) {
		this.eventSerializer = serializer;
	}
	
    public void setSnapshotSerializer(Serializer<? super EventSourcingAggregateRoot, U> serializer) {
      this.snapshotSerializer = serializer;
    }	
	
}
