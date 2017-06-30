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

import org.cqrs.core.CQRS;
import org.cqrs.example.domain.FinalBankAccount;
import org.cqrs.example.handler.BankAccountHandler.BankAccountCmd;
import org.cqrs.example.handler.FinalBankAccountHandler;

import com.google.common.collect.ImmutableMap;

public class BankAccountTest {

  public static void main(String[] args) throws InterruptedException {
    
    CQRS cqrs = CQRS.get();
    new FinalBankAccountHandler(FinalBankAccount.class);
    
    String id = cqrs.execute(BankAccountCmd.OPEN_ACCOUNT, ImmutableMap.of("name", "wrd", "balance", 100.00));
    
//    for (int i = 0; i < 500; i++) {
//      cqrs.execute(BankAccountCmd.DEPOSITE_MONEY, ImmutableMap.of("id", id, "amount", Math.random() * 100));
//    }
    cqrs.execute(BankAccountCmd.DEPOSITE_MONEY, ImmutableMap.of("id", id, "amount", Math.random() * 100));
    
//    cqrs.execute(BankAccountCmd.WITHDRAWAL_MONEY, ImmutableMap.of("id", id, "amount", 101.00));
    
    System.out.println("========================================");
    
    System.out.println(cqrs.domain(FinalBankAccount.class).aggregateRoot(id));
    
    cqrs.shutdown();
  }
}
