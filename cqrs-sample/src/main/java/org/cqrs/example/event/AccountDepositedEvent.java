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

import org.cqrs.core.DomainEvent;

/**
 * @author weird
 */
public class AccountDepositedEvent extends DomainEvent implements Serializable {

  private static final long serialVersionUID = -5448179048698799001L;
  public final Double amount;

  public AccountDepositedEvent(Double amount) {
    super();
    this.amount = amount;
  }
}
