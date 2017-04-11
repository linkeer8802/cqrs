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

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.cqrs.core.DomainEvent;
import org.cqrs.core.EventSourcingAggregateRoot;

public class InMemoryEventStorage extends EventStorage {

	private Map<String, NavigableMap<Long, EventData>> events = new HashMap<>();
	private Map<String, SnapshotData> snapshots = new HashMap<>();
	
	@Override
	public void appendEvent(DomainEvent event) {
		
		EventSourcingAggregateRoot root = (EventSourcingAggregateRoot) event.getAggregateRoot();
				
		EventData data = new EventData(root.getId(), 
				System.currentTimeMillis(), root.getVersion(), serializer.serialize(event));
		
		if (!events.containsKey(root.getId())) {
			events.put(root.getId(), new TreeMap<>());
		}
		events.get(root.getId()).put(data.getVersion(), data);
	}

	@Override
	public NavigableMap<Long, DomainEvent> readEvents(String aggregateId, long fromVersion) {
		NavigableMap<Long, EventData> map = events.get(aggregateId);
		NavigableMap<Long, DomainEvent> events = new TreeMap<>();
		map.tailMap(fromVersion).forEach((key, value) -> {
			events.put(key, (DomainEvent) serializer.deserialize(value.getData()));
		});
		return events;
	}

	@Override
	public void storeSnapshot(EventSourcingAggregateRoot root) {
		snapshots.put(root.getId(), new SnapshotData(root.getId(), 
				root.getClass().getName(), root.getVersion(), serializer.serialize(root)));
	}

	@Override
	public void deleteSnapshots(String aggregateId) {
		snapshots.remove(aggregateId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends EventSourcingAggregateRoot> T getAggregateRootFromSnapshot(String aggregateId) {
		if (!snapshots.containsKey(aggregateId)) {
			return null;
		}
				
		return (T) serializer.deserialize(snapshots.get(aggregateId).getData());
	}
}
