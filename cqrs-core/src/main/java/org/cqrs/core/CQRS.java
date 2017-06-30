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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.cqrs.core.eventbus.EventBus;
import org.cqrs.core.eventbus.impl.DisruptorEventBusImpl;
import org.cqrs.core.eventbus.impl.SimpleEventBusImpl;
import org.cqrs.core.eventstore.EventRepository;
import org.cqrs.core.eventstore.EventStorage;
import org.cqrs.core.eventstore.JdbcEventStorage;
import org.cqrs.core.eventstore.JsonSerializer;
import org.cqrs.core.eventstore.LambdaEventSourcingRepository;
import org.cqrs.core.journals.EventsJournalsMessageInterceptor;
import org.cqrs.core.journals.InMemoryJournalsStorage;
import org.cqrs.core.journals.JournalsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author weird
 */
public class CQRS {

  public static final int DEFAULT_BUFFER_SIZE = 1024;
  
  static final Logger logger = LoggerFactory.getLogger(CQRS.class);
  
  private static CQRS instance = new CQRS();
  private volatile Boolean started;
  private ComponentManager componentManager;
  
  private int bufferSize;
  private ExecutorService executorService;
  private EventBus commandBus;
  private EventBus replayEventBus;
  private EventBus eventBus;
  private EventRepository repository;
  
  private JournalsStorage journalsStorage;
  
  private Map<Class<?>, DomainContext<?>> domainContextMaps;
  
  public CQRS() {
    started = false;
    domainContextMaps = new HashMap<>();
  }
  
  public static CQRS get() {
    if (instance.started) {
      return instance;
    }
    return instance.startup();
  }
  
  public CQRS startup() {
    
    if (!started) {
      started = true;
      
      init();
      logger.info("CQRS engine startup initiated.");
      
      componentManager.startAll();
      
      logger.info("CQRS engine is started.");
    }
    
    return this;
  }

  private void init() {
    
    bufferSize = DEFAULT_BUFFER_SIZE;
    componentManager = new ComponentManager();
    
    executorService = Executors.newCachedThreadPool();
    commandBus = componentManager.addComponent(new DisruptorEventBusImpl(executorService, bufferSize));
    replayEventBus = componentManager.addComponent(new DisruptorEventBusImpl(executorService, bufferSize));

    eventBus = new SimpleEventBusImpl();
    journalsStorage = new InMemoryJournalsStorage();
    eventBus.addMessageInterceptor(new EventsJournalsMessageInterceptor(journalsStorage));
    
//    InMemoryEventStorage eventStorage = new InMemoryEventStorage();
//    eventStorage.setEventSerializer(new JavaBuildInSerializer<DomainEvent>());
//    eventStorage.setSnapshotSerializer(new JavaBuildInSerializer<EventSourcingAggregateRoot>());
    
    EventStorage<String> eventStorage = new JdbcEventStorage();
    eventStorage.setEventSerializer(new JsonSerializer<DomainEvent>());
    eventStorage.setSnapshotSerializer(new JsonSerializer<EventSourcingAggregateRoot>());
    
//    repository = new EventSourcingRepository(eventStorage, journalsStorage);
    repository = new LambdaEventSourcingRepository(eventStorage, journalsStorage);
  }
  
  public void shutdown() {

    componentManager.stopAll();
    executorService.shutdown();
    
    started = false;
    
    logger.info("CQRS engine has been stoped.");
  }
  
  @SuppressWarnings("unchecked")
  public <T extends EventSourcingAggregateRoot> DomainContext<T> domain(Class<T> type) {
    DomainContext<T> domainContext = (DomainContext<T>) domainContextMaps.get(type);
    if (domainContext == null) {
      domainContext = new DomainContext<T>(type, commandBus, replayEventBus, eventBus, repository);
      domainContextMaps.put(type, domainContext);
    }
    return domainContext;
  }
  
  public <T extends EventSourcingAggregateRoot> void applyEvent(T aggregateRoot, DomainEvent event) {
    // Apply this event to current aggregateRoot and then the return aggregateRoot is newest state.
    event.aggregateRoot = replayEvent(aggregateRoot, event);
    
    repository.saveEvent(event);
    publishEvent(event.getClass().getName(), event);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends EventSourcingAggregateRoot> T replayEvent(T aggregateRoot, DomainEvent event) {
    event.aggregateRoot = aggregateRoot;
    return (T) replayEventBus
    .execute(event.getClass().getName() + DomainContext.EVENT_REPLAY_SUFFIX, event);
  }
  
  public void publishEvent(String address, DomainEvent event) {
    eventBus.publish(address, event);
  }
  
  public <T> CompletableFuture<T> send(Object name, Object command) {
    if (name == null || command == null) {
      throw new IllegalArgumentException("The argument 'name' and 'command' must to be not null.");
    }
    return commandBus.send(name.toString(), command, new CompletableFuture<T>());
  }
  
  @SuppressWarnings("unchecked")
  public <T> T execute(Object name, Object command) {
    if (name == null || command == null) {
      throw new IllegalArgumentException("The argument 'name' and 'command' must to be not null.");
    }
    return (T) commandBus.execute(name.toString(), command);
  }
}
