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

package org.cqrs.example;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.cqrs.core.CQRS;
import org.cqrs.example.domain.FinalBankAccount;
import org.cqrs.example.domain.TransferTransaction;
import org.cqrs.example.handler.FinalBankAccountHandler;
import org.cqrs.example.handler.FinalBankAccountHandler.BankAccountCmd;
import org.cqrs.example.handler.TransferTransactionHandler;
import org.cqrs.example.handler.TransferTransactionHandler.TransferTransactionCmd;

import com.google.common.collect.ImmutableMap;

public class BankAccountTransferTest {

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    
    CQRS cqrs = CQRS.get();
    new FinalBankAccountHandler(FinalBankAccount.class);
    new TransferTransactionHandler(TransferTransaction.class);
    
    
    String sourceId = cqrs.execute(BankAccountCmd.OPEN_ACCOUNT, ImmutableMap.of("name", "wrd", "balance", 1000.00));
    String targetId = cqrs.execute(BankAccountCmd.OPEN_ACCOUNT, ImmutableMap.of("name", "linkeer", "balance", 100.00));
    
    String transactionId = cqrs.execute(TransferTransactionCmd.BEGIN_START_TRANSFER, 
        ImmutableMap.of(
            "sourceId", sourceId, 
            "targetId", targetId, 
            "amount", 500.00, 
            "transferDateTime", 
            new Date()));
    
    System.out.println("transactionId:" + transactionId);
    
    Thread.sleep(500);
    
    System.out.println(cqrs.domain(FinalBankAccount.class).aggregateRoot(sourceId));
    System.out.println(cqrs.domain(FinalBankAccount.class).aggregateRoot(targetId));
    
    cqrs.shutdown();
  }
}
