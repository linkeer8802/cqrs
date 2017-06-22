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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.cqrs.core.eventbus.EventBus;
import org.cqrs.core.eventbus.Message;
import org.cqrs.core.eventbus.MessageConsumer;
import org.cqrs.core.eventbus.ReplyableMessage;
import org.cqrs.core.eventstore.EventRepository;

/**
 * @author weird
 */
public class DomainContext<T extends EventSourcingAggregateRoot> {
  
  public static final String EVENT_REPLAY_SUFFIX = ":event-replay";
  
  private EventBus eventBus;
  private EventBus commandBus;
  private EventBus ReplayEventBus;
  private EventRepository repository;
  private Class<T> domainType;
  
  @SuppressWarnings("rawtypes")
  private List<SagaHandlerContainer> sagaHandlers;
  
  public DomainContext(
      Class<T> domainType, 
      EventBus commandBus, 
      EventBus eventReplayCommandBus,
      EventBus eventBus, 
      EventRepository repository) {
    super();
    this.eventBus = eventBus;
    this.domainType = domainType;
    this.commandBus = commandBus;
    this.repository = repository;
    this.ReplayEventBus = eventReplayCommandBus;
    this.sagaHandlers = new ArrayList<>();
  }

  public DomainContext<T> onCmd(Object address, BiConsumer<CommandContext, T> consumer) {
    
    if (address == null) {
      throw new IllegalArgumentException("The argument 'address' must to be assign.");
    }
    
    commandBus.subscribe(address.toString(), message -> {
      
      handleEvent(message, (context, aggregateRoot) -> {
        consumer.accept(context, aggregateRoot); 
        return null;
      });
      
      if (message instanceof ReplyableMessage) {
        ((ReplyableMessage<?>)message).reply(ReplyableMessage.emptyMessage);
      }
    });
    
    return this;
  }
  
  public DomainContext<T> onCmd(Object address, MessageConsumer<CommandContext, T> consumer) {
    
    if (address == null) {
      throw new IllegalArgumentException("The argument 'address' must to be assign.");
    }
    
    commandBus.subscribe(address.toString(), message -> {
      ((ReplyableMessage<?>)message).reply(handleEvent(message, consumer));
    });
    
    return this;
  }
  
  @SuppressWarnings("unchecked")
  private Object handleEvent(Message<?> message, MessageConsumer<CommandContext, T> consumer) {
    T aggregateRoot = null;
    String aggregateId = null;
    
    if (message.getBody() instanceof Map) {
      Map<String, Object> commandMap =  Map.class.cast(message.getBody());
      aggregateId = (String) commandMap.get("id");
    } else {
      //TODO 其他类型
    }
    
    if (aggregateId != null) {
      aggregateRoot = repository.get(domainType, aggregateId);
    }
    CommandContext context = new CommandContext(message.getBody());
    
    return consumer.handle(context, aggregateRoot);
  }
  
  @SuppressWarnings("unchecked")
  public <E> DomainContext<T> onEvent(Class<E> eventType, Consumer<E> consumer) {
    
    eventBus.subscribe(eventType.getName(), message -> {
      consumer.accept((E) message.getBody());
    });
    
    return this;
  }
  
  @SuppressWarnings("unchecked")
  public <E extends DomainEvent> DomainContext<T> onEvent(Class<E> eventType, BiConsumer<EventContext, E> consumer) {
    
    eventBus.subscribe(eventType.getName(), message -> {
      consumer.accept(new EventContext(), (E) message.getBody());
    });
    
    return this;
  }
  
  @SuppressWarnings("unchecked")
  public <E extends DomainEvent> DomainContext<T> onReplay(Class<E> eventType, MessageConsumer<T, E> consumer) {
    
    ReplayEventBus.subscribe(eventType.getName() + EVENT_REPLAY_SUFFIX, message -> {
      E event = (E) message.getBody();
      T aggregateRoot = (T) event.aggregateRoot;
      
      ((ReplyableMessage<?>)message).reply(((T) consumer.handle(aggregateRoot, event)).incrVersion(aggregateRoot));
    });
    
    return this;
  }
  
  @SuppressWarnings("unchecked")
  public <E extends DomainEvent> DomainContext<T> onSagaStart(Class<E> eventType, MessageConsumer<SagaContext, E> consumer, BiConsumer<SagaContext, E> callback) {
    
    ReplayEventBus.subscribe(eventType.getName() + EVENT_REPLAY_SUFFIX, message -> {
      E event = (E) message.getBody();
      T aggregateRoot = (T) event.aggregateRoot;
      SagaContext context = new SagaContext();
      
      EventSourcingAggregateRoot target = ((T) consumer.handle(context, event)).incrVersion(aggregateRoot);
      
      sagaHandlers.stream().forEach((handlerContainer) -> {
        eventBus.subscribeOnce(handlerContainer.getEventType().getName(), (_message) -> {
          Object property = context.attrs.get(handlerContainer.getAssociationProperty());
          E _event = (E) _message.getBody();
          T _aggregateRoot = (T) _event.aggregateRoot;
          if (_aggregateRoot != null && _aggregateRoot.id.equals(property)) {
            handlerContainer.getConsumer().accept(context, _event);
          }
        });
      });
      
      if (aggregateRoot == null || !aggregateRoot.isReplay) {
        callback.accept(context, event);
      }
      
      ((ReplyableMessage<?>)message).reply(target);
    });
    
    return this;
  }
  
  public <E extends DomainEvent> DomainContext<T> onSaga(Class<E> eventType, String associationProperty, BiConsumer<SagaContext, E> consumer) {
    sagaHandlers.add(new SagaHandlerContainer<E>(eventType, associationProperty, consumer));
    return this;
  }
  
  public T aggregateRoot(String aggregateId) {
    return repository.get(domainType, aggregateId);
  }
}
