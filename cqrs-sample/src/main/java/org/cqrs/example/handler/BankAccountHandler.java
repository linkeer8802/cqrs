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
import org.cqrs.example.domain.BankAccount;
import org.cqrs.example.event.AccountDepositedEvent;
import org.cqrs.example.event.AccountOpenedEvent;
import org.cqrs.example.event.AccountWithdrawedEvent;
import org.cqrs.example.exception.BalanceNotEnoughException;

/**
 * @author weird
 */
public class BankAccountHandler extends DomainHandler<BankAccount>{

  public static enum BankAccountCmd {
    /**开户**/
    OPEN_ACCOUNT,
    /**存款**/
    DEPOSITE_MONEY,
    /**取款**/
    WITHDRAWAL_MONEY
  }
  
  public BankAccountHandler(Class<BankAccount> domainType) {
    super(domainType);
  }

  public void handle(DomainContext<BankAccount> domain) {
    /**
     * 账号开户
     */
    domain.onCmd(BankAccountCmd.OPEN_ACCOUNT, (context, bankAccount) -> {
      String id = context.uniqueId();
      context.applyEvent(bankAccount, new AccountOpenedEvent(
          id, context.strArg("name"), context.doubleArg("balance")));
      return id;
      
    }).onReplay(AccountOpenedEvent.class, (bankAccount, event) -> {
      return new BankAccount(event.aggregateRootId, event.name, event.balance);
      
    }).onEvent(AccountOpenedEvent.class, (event) -> {
      System.out.println(event.name + " open accout");
    });
    /**
     * 账号存款
     */
    domain.onCmd(BankAccountCmd.DEPOSITE_MONEY, (context, bankAccount) -> {
      context.applyEvent(bankAccount, new AccountDepositedEvent(context.doubleArg("amount")));
      
    }).onReplay(AccountDepositedEvent.class, (bankAccount, event)-> {
      return bankAccount.deposit(event.amount);
      
    }).onEvent(AccountDepositedEvent.class, (event) -> {
      System.out.println("Deposited money " + event.amount);
    });
    /**
     * 账号取款
     */
    domain.onCmd(BankAccountCmd.WITHDRAWAL_MONEY, (context, bankAccount) -> {
      Double amount = context.doubleArg("amount");
      if (bankAccount.getBalance() < amount) {
        throw new BalanceNotEnoughException("账户余额不足");
      }
      context.applyEvent(bankAccount, new AccountWithdrawedEvent(amount));
      
    }).onReplay(AccountWithdrawedEvent.class, (bankAccount, event)-> {
      return bankAccount.withdrawal(event.amount);
      
    }).onEvent(AccountWithdrawedEvent.class, (event) -> {
      System.out.println("Withdrawed money " + event.amount);
    });
  }
}
