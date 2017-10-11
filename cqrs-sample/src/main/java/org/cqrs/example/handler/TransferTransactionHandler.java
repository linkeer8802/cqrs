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

package org.cqrs.example.handler;

import org.cqrs.core.DomainContext;
import org.cqrs.core.DomainHandler;
import org.cqrs.example.domain.TransactionState;
import org.cqrs.example.domain.TransferTransaction;
import org.cqrs.example.event.TransactionStartedEvent;
import org.cqrs.example.event.TransferedOutEvent;
import org.cqrs.example.handler.FinalBankAccountHandler.BankAccountCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * @author weird
 */
public class TransferTransactionHandler extends DomainHandler<TransferTransaction>{

  static final Logger logger = LoggerFactory.getLogger(TransferTransactionHandler.class);
  
  public static enum TransferTransactionCmd {
    BEGIN_START_TRANSFER
  }
  
  public TransferTransactionHandler(Class<TransferTransaction> domainType) {
    super(domainType);
  }

  @Override
  public void handle(DomainContext<TransferTransaction> domainContext) {
    
    domainContext.onCmd(TransferTransactionCmd.BEGIN_START_TRANSFER, (context, transferTransaction) -> {
      String id = context.uniqueId();
      context.applyEvent(transferTransaction, 
          new TransactionStartedEvent(
          id, 
          context.strArg("sourceId"), 
          context.strArg("targetId"), 
          context.doubleArg("amount"), 
          context.dateArg("transferDateTime")));
      
      return id;
      
    }).onEvent(TransactionStartedEvent.class, (context, event) -> {
      
      System.out.println("Start transfer money from sourceId " + event.sourceId 
          + " to targetId " + event.targetId + " with amount " + event.amount);
      
    }).onSagaStart(TransactionStartedEvent.class, (context, event) -> {
      
      context.associateWith("sourceId", event.sourceId);
      context.associateWith("targetId", event.targetId);
      
      return new TransferTransaction(event.aggregateRootId, event.sourceId, 
          event.targetId, event.amount, event.transferDateTime, TransactionState.INIT);
      
    }, (context, event) -> {
      
      context.send(
          BankAccountCmd.TRANSFERED_OUT, 
          ImmutableMap.of("id", event.sourceId, "amount", event.amount));
      
    }).onSaga(TransferedOutEvent.class, "sourceId", (context, event) -> {
      
      context.send(BankAccountCmd.TRANSFERED_IN, 
          ImmutableMap.of("id", (String)context.associationProperty("targetId"), "amount", event.amount))
      .whenComplete((result, ex) -> {
        if (ex != null) {
          logger.error("Deposite money error, cause by {}", ex.getMessage());
          
          context.send(BankAccountCmd.ROLLBACK_TRANSFER_OUT, 
              ImmutableMap.of("id", (String)context.associationProperty("sourceId"), "amount", event.amount));
        } else {
          context.end();
        }
      }); 
      
    });
  }
}
