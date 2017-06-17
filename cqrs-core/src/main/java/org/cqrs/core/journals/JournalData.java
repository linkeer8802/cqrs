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

package org.cqrs.core.journals;


/**
 * @author weird
 */
public class JournalData {

  private String aggregateRootId;
  private Long aggregateRootVersion;
  
  public JournalData(String aggregateRootId, Long aggregateRootVersion) {
    super();
    this.aggregateRootId = aggregateRootId;
    this.aggregateRootVersion = aggregateRootVersion;
  }

  public String getAggregateRootId() {
    return aggregateRootId;
  }
  
  public void setAggregateRootId(String aggregateRootId) {
    this.aggregateRootId = aggregateRootId;
  }
  
  public Long getAggregateRootVersion() {
    return aggregateRootVersion;
  }
  
  public void setAggregateRootVersion(Long aggregateRootVersion) {
    this.aggregateRootVersion = aggregateRootVersion;
  }
}
