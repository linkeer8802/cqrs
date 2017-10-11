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

package org.cqrs.util;

import java.io.IOException;
import java.util.Properties;

/**
 * @author weird
 */
public class PropertiesCfg {
  
  private Properties props;
  private static PropertiesCfg instance;
  
  public PropertiesCfg() {
    props = new Properties();
  }

  public static PropertiesCfg load(String name) {
    instance = new PropertiesCfg();
    try {
      instance.props.load(PropertiesCfg.class.getClassLoader().getResourceAsStream(name));
    } catch (IOException e) {
      throw new IllegalStateException("Load properties resource fail.", e);
    } 
    
    return instance;
  }
  
  public String get(String name) {
    return props.getProperty(name);
  }
  
  public Integer getIntValue(String name) {
    return Integer.valueOf(props.getProperty(name));
  }
  
  public Boolean getBoolValue(String name) {
    return Boolean.valueOf(props.getProperty(name));
  }
  
  public Long getLongValue(String name) {
    return Long.valueOf(props.getProperty(name));
  }
  
  public Integer getInt(String name) {
    return Integer.valueOf(props.getProperty(name));
  }
}
