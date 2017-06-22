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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author weird
 */
public class ReplyableMessage<T> extends Message<T>{
  
  static final Logger logger = LoggerFactory.getLogger(ReplyableMessage.class);
  
  public static final String HEADER_MESSAGE_RSP = ":rsp";
  public static final Object emptyMessage = new EmptyMessage();
  
  private volatile boolean isReply = false;
  protected transient EventBus bus;

  public ReplyableMessage(EventBus bus, Class<T> type, T body, boolean send) {
    super(type, body, send);
    this.bus = bus;
  }
  
  public void reply(Object message) {
    if (!isReply) {
      
      logger.debug("Reply message, address={}", getAddress());
      
      isReply = true;
      bus.send(getReplyAddress() , message);
    }
  }

  public void fail(Throwable e) {
    reply(e);
  }
  
  public String getReplyAddress() {
    return getMessageId() + HEADER_MESSAGE_RSP;
  }

 static class EmptyMessage {}
}
