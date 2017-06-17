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

package org.cqrs.example.event;

import java.io.Serializable;
import java.util.Date;

import org.cqrs.core.DomainCreatedEvent;

/**
 * @author weird
 */
public class TransactionStartedEvent extends DomainCreatedEvent implements Serializable{

  private static final long serialVersionUID = -7602220148315963287L;
  public final String sourceId;
  public final String targetId;
  public final Double amount;
  public final Date transferDateTime;
  
  public TransactionStartedEvent(
      String aggregateRootId, 
      String sourceId, 
      String targetId, 
      Double amount,
      Date transferDateTime) {
    super(aggregateRootId);
    this.sourceId = sourceId;
    this.targetId = targetId;
    this.amount = amount;
    this.transferDateTime = transferDateTime;
  }
}
