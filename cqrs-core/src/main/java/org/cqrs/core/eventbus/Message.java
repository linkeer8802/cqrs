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

package org.cqrs.core.eventbus;

import java.util.HashMap;
import java.util.Map;

import org.cqrs.util.SequenceUUID;

/**
 * @author weird
 */
public class Message<T> {

  public static final String HEADER_MESSAGE_ADDR = "HEADER_MESSAGE_ADDRESS";
  public static final String HEADER_MESSAGE_SEND_FLAG = "HEADER_MESSAGE_SEND";
  
  private Long timestamp;
  private String messageId;
  private Class<T> type;
  private boolean send;
  private Map<String, Object> headers;
  
  public Object getHeader(String key) {
    return headers.get(key);
  }

  private T body;
  
  public Message(Class<T> type, T body, boolean send) {
    super();
    this.type = type;
    this.body = body;
    this.send = send;
    this.timestamp= System.currentTimeMillis();
    this.messageId = SequenceUUID.get().toString();
    this.headers = new HashMap<>();
  }
  
  public Long getTimestamp() {
    return timestamp;
  }
  
  public T getBody() {
    return body;
  }
  
  public String getMessageId() {
    return messageId;
  }
  
  public Class<T> getType() {
    return type;
  }
  
  public Message<T> addHeader(String key, Object value) {
    headers.put(key, value);
    return this;
  }
  
  public String getAddress() {
    return (String) headers.get(HEADER_MESSAGE_ADDR);
  }
  
  public boolean isSend() {
    return send;
  }
  
  public void setSend(boolean send) {
    this.send = send;
  }
}
