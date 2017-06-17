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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryJournalsStorage implements JournalsStorage {

  static final Logger logger = LoggerFactory.getLogger(InMemoryJournalsStorage.class);
  
  private Map<String, JournalData> journals;
  
  public InMemoryJournalsStorage() {
    journals = new HashMap<>();
  }
  
  @Override
  public void submit(String id, Long version) {
    
    logger.debug("To be submitted domain event, aggregateRootId={}, version={}", id, version);
    
    JournalData data = journals.get(id);
    if (data == null) {
      journals.put(id, new JournalData(id, version));
    } else {
      data.setAggregateRootVersion(version);
    }
  }

  @Override
  public Long getLastVersion(String id) {
    JournalData data = journals.get(id);
    if (data == null) {
      return null;
    } else {
      return data.getAggregateRootVersion();
    }
  }
}
