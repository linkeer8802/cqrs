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

package org.cqrs.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weird
 */
public class ComponentManager {

  List<Component> components;
  
  public ComponentManager() {
    components = new ArrayList<>();
  }
  
  public <T> T addComponent(T component) {
    if (component instanceof Component) {
      components.add(Component.class.cast(component));
    } else {
      throw new IllegalArgumentException("The object must to be instanceof component.");
    }
    return component;
  }
  
  public void startAll() {
    components.stream().forEach((component) -> {component.start();});
  }
  
  public void stopAll() {
    components.stream().forEach((component) -> {component.shutdown();});
  }
}
