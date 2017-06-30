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

import java.util.concurrent.CompletableFuture;

/**
 * @author weird
 */
public interface EventBus {
  /**
   * 发布消息到指定的address，这个address可以被多个订阅者订阅。
   * @param address
   * @param message
   */
  void publish(String address, Object message);
  /**
   * 发送消息到指定的address，这个address一般只可以被1个订阅者订阅（多个时随机处理一个）。
   * @param address
   * @param message
   */
  void send(String address, Object message);
  /**
   * 和{@link #send(String, Object)}一样，同时可注册一个回复结果消息的replyhandler。
   * @param address
   * @param message
   * @param replyhandler
   * @return
   */
  <T> CompletableFuture<T> send(String address, Object message, CompletableFuture<T> future);
  /**
   * 和{@link #send(String, Object)}一样，但该方法是同步运行并且返回一个回复结果的消息。
   * @param address
   * @param message
   * @return
   */
  <T> T execute(String address, Object message);
  /**
   * 订阅指定address上的消息
   * @param address
   * @param handler
   */
  void subscribe(String address, MessageHandler<?> handler);
  /**
   * 只订阅一次指定address上的消息，处理完成后取消订阅此消息。
   * @param address
   * @param handler
   */
  void subscribeOnce(String address, MessageHandler<?> handler);
  /**
   * 取消订阅指定address上的消息
   * @param address
   */
  void unsubscribe(String address);
  /**
   * 取消订阅指定address上关联handler的消息
   * @param address
   * @param handler
   */
  void unsubscribe(String address, MessageHandler<?> handler);
  /**
   * 添加一个消息处理拦截器
   * @param interceptor
   */
  EventBus addMessageInterceptor(MessageInterceptor interceptor);
}
