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
import org.cqrs.example.domain.FinalBankAccount;
import org.cqrs.example.event.AccountOpenedEvent;
import org.cqrs.example.event.TransferOutRolledbackEvent;
import org.cqrs.example.event.TransferedInEvent;
import org.cqrs.example.event.TransferedOutEvent;
import org.cqrs.example.exception.BalanceNotEnoughException;

/**
 * @author weird
 */
public class FinalBankAccountHandler extends DomainHandler<FinalBankAccount>{

  public static enum BankAccountCmd {
    /**开户**/
    OPEN_ACCOUNT,
    /**转入**/
    TRANSFERED_IN,
    /**转出**/
    TRANSFERED_OUT,
    /**回滚转出**/
    ROLLBACK_TRANSFER_OUT
  }
  
  public FinalBankAccountHandler(Class<FinalBankAccount> domainType) {
    super(domainType);
  }

  public void handle(DomainContext<FinalBankAccount> domain) {
    /**
     * 账号开户
     */
    domain.onCmd(BankAccountCmd.OPEN_ACCOUNT, (context, bankAccount) -> {
      
      String id = context.uniqueId();
      
      context.publishEvent(bankAccount, new AccountOpenedEvent(
          id, context.strArg("name"), context.doubleArg("balance")));
      
      return id;
      
    }).onReplay(AccountOpenedEvent.class, (bankAccount, event) -> {
      
      return new FinalBankAccount(event.aggregateRootId, event.name, event.balance);
      
    }).onEvent(AccountOpenedEvent.class, (event) -> {
      
      System.out.println(event.name + " open accout");
    });
    /**
     * 转入
     */
    domain.onCmd(BankAccountCmd.TRANSFERED_IN, (context, bankAccount) -> {
      
      Double amount = context.doubleArg("amount");
      
      if (amount >= 500) {
        throw new IllegalStateException("单笔交易额超上限。");
      }
      
      context.publishEvent(bankAccount, new TransferedInEvent(amount));
      
      System.out.println("----------------------------------------");
      
    }).onReplay(TransferedInEvent.class, (bankAccount, event)-> {
      
      return bankAccount.transferedIn(event.amount);
      
    }).onEvent(TransferedInEvent.class, (event) -> {
      
      System.out.println("Transfered in money " + event.amount);
    });
    /**
     * 转出
     */
    domain.onCmd(BankAccountCmd.TRANSFERED_OUT, (context, bankAccount) -> {
      
      Double amount = context.doubleArg("amount");
      
      if (bankAccount.balance < amount) {
        throw new BalanceNotEnoughException("账户余额不足");
      }
      
      context.publishEvent(bankAccount, new TransferedOutEvent(amount));
      
    }).onReplay(TransferedOutEvent.class, (bankAccount, event)-> {
      
      return bankAccount.withdrawal(event.amount);
      
    }).onEvent(TransferedOutEvent.class, (event) -> {
      
      System.out.println("Transfered out money " + event.amount);
    });
    /**
     * 回滚转出
     */
    domain.onCmd(BankAccountCmd.ROLLBACK_TRANSFER_OUT, (context, bankAccount) -> {
      
      Double amount = context.doubleArg("amount");
      
      context.publishEvent(bankAccount, new TransferOutRolledbackEvent(amount));
      
    }).onReplay(TransferOutRolledbackEvent.class, (bankAccount, event)-> {
      
      return bankAccount.rollbackRransferout(event.amount);
      
    }).onEvent(TransferOutRolledbackEvent.class, (event) -> {
      
      System.out.println("Rollback transfered out money " + event.amount);
    });
  }
}
