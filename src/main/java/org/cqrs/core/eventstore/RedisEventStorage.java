//package com.pzedu.infrastructure.support.cqrs.eventstore;
//
//import java.util.NavigableMap;
//import java.util.Set;
//
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
//
//import com.google.common.collect.Maps;
//import com.pzedu.infrastructure.support.cqrs.DomainEvent;
//import com.pzedu.infrastructure.support.cqrs.EventSourcingAggregateRoot;
//
//public class RedisEventStorage extends EventStorage {
//
//	public static final String EVENT_KEY_PREFIX = "ES_EVENT_";
//	public static final String SNAPSHOT_KEY_PREFIX = "ES_SNAPSHOT_";
//	
//	private RedisTemplate<String, Object> redisTemplate;
//	
//	@Override
//	public void appendEvent(DomainEvent event) {
//		
//		EventSourcingAggregateRoot root = (EventSourcingAggregateRoot) event.getAggregateRoot();
//		String aggregateId = getAggregateId(root, event);
//		
//		EventData data = new EventData(aggregateId, 
//				System.currentTimeMillis(), root.nextVersion(), serializer.serialize(event));
//		
//		redisTemplate.opsForZSet().add(EVENT_KEY_PREFIX + aggregateId, data, data.getVersion());
//	}
//
//	@Override
//	public NavigableMap<Long, DomainEvent> readEvents(String aggregateId, long lastVersion) {
//		
//		NavigableMap<Long, DomainEvent> events = Maps.newTreeMap();
//		Set<TypedTuple<Object>> items = redisTemplate.opsForZSet()
//				.rangeWithScores(EVENT_KEY_PREFIX + aggregateId, lastVersion, -1);
//		items.forEach(item -> {
//			events.put(item.getScore().longValue(), 
//					(DomainEvent) serializer.deserialize(((EventData)item.getValue()).getData()));
//		});
//		
//		return events;
//	}
//
//	@Override
//	public void storeSnapshot(EventSourcingAggregateRoot root) {
//		redisTemplate.opsForZSet().add(SNAPSHOT_KEY_PREFIX + root.getId(), 
//				new SnapshotData(root.getId(), root.getClass().getName(),
//						root.getVersion(), serializer.serialize(root)), root.getVersion());
//	}
//
//	@Override
//	public void deleteSnapshots(String aggregateId) {
//		redisTemplate.delete(SNAPSHOT_KEY_PREFIX + aggregateId);
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public <T extends EventSourcingAggregateRoot> T getAggregateRootFromSnapshot(String aggregateId) {
//		
//		Set<Object> snapshots = redisTemplate.opsForZSet().range(SNAPSHOT_KEY_PREFIX + aggregateId, -1, -1);
//		
//		if (snapshots.isEmpty()) {
//			return null;
//		}
//				
//		return (T) serializer.deserialize(((SnapshotData)snapshots.toArray()[0]).getData());
//	}
//	
//	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
//		this.redisTemplate = redisTemplate;
//	}
//}
