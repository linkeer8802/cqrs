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

import org.cqrs.core.EventSourcingAggregateRoot;

/**
 * 银行账号
 * @author weird
 */
public class FinalBankAccount extends EventSourcingAggregateRoot{

  public final String name;
  public final Double balance;
  
  public FinalBankAccount(String id, String name, Double balance) {
    super(id);
    this.name = name;
    this.balance = balance;
  }
  /**
   * 存入指定金额
   * @param amount 要存入的金额
   * @return 
   */
  public FinalBankAccount deposit(double amount) {
    return new FinalBankAccount(id, name, balance + amount);
  }
  /**
   * 取出指定金额
   * @param amount 要取出的金额
   * @return 
   */
  public FinalBankAccount withdrawal(double amount) {
    return new FinalBankAccount(id, name, balance - amount);
  }  
  /**
   * 转出指定金额
   * @param amount 要转出的金额
   * @return 
   */
  public FinalBankAccount transferedOut(double amount) {
    return new FinalBankAccount(id, name, balance - amount);
  } 
  /**
   * 转入指定金额
   * @param amount 要转入的金额
   * @return 
   */
  public FinalBankAccount transferedIn(double amount) {
    return new FinalBankAccount(id, name, balance + amount);
  }
  
  /**
   * 回滚转出的金额
   * @param amount 要回滚的金额
   * @return 
   */
  public FinalBankAccount rollbackRransferout(double amount) {
    return new FinalBankAccount(id, name, balance + amount);
  }
  
  @Override
  public String toString() {
    return "BankAccount [name=" + name + ", balance=" + balance + ", id=" + id + "]";
  }
}
