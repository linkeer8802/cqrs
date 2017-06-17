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

package org.cqrs.example.domain;

import java.util.Date;

import org.cqrs.core.EventSourcingAggregateRoot;

/**
 * 转账交易
 * @author weird
 */
public class TransferTransaction extends EventSourcingAggregateRoot{
  
  public final String sourceId;
  public final String targetId;
  public final Double amount;
  public final Date transferDateTime;
  public final TransactionState state;

  public TransferTransaction(
      String id, 
      String sourceId, 
      String targetId, 
      Double amount, 
      Date transferDateTime,
      TransactionState state) {
    super(id);
    this.amount = amount;
    this.state = state;
    this.sourceId = sourceId;
    this.targetId = targetId;
    this.transferDateTime = transferDateTime;
  }

  
//  /**
//   * 从源账号转出指定金额并转入到目标账号
//   * @param target 源账号
//   * @param target 目标账号
//   * @param amount 交易金额
//   * @return
//   */
//  public TransferTransaction transfer(BankAccount source, BankAccount target, double amount) {
//    source.withdrawal(amount);
//    target.deposit(amount);
//    return this;
//  }
}
