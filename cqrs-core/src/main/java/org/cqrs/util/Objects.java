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

import java.lang.reflect.Field;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.internal.UnsafeAllocator;

public class Objects {

  public static <T> T create(Class<T> type) {
    try {
      return UnsafeAllocator.create().newInstance(type);
    } catch (Exception e) {
      throw new RuntimeException("create object error.", e);
    }
  }

  public static <T> T init(Class<T> type, Object... fields) {
    try {
      T object = create(type);
      Field[] declaredFields = type.getDeclaredFields();
      for (int i = 0; i < declaredFields.length; i++) {
        declaredFields[i].setAccessible(true);
        ;
        declaredFields[i].set(object, fields.length > i ? fields[i] : null);
      }
      return object;
    } catch (Exception e) {
      throw new RuntimeException("create object error.", e);
    }
  }

  public static <T> T init(Class<T> type, ImmutableMap<String, Object> fields) {
    try {
      T object = create(type);

      for (Field field : getAllDeclaredFields(type)) {
        if (fields.containsKey(field.getName())) {
          field.setAccessible(true);
          field.set(object, fields.get(field.getName()));
        }
      }

      return object;
    } catch (Exception e) {
      throw new RuntimeException("create object error.", e);
    }
  }
  
  public static List<Field> getAllDeclaredFields(Class<?> clazz) {
    
    Class<?> targetClass = clazz;
    List<Field> allFields = Lists.newArrayList();
   
    do {
      Field[] fields = targetClass.getDeclaredFields();
      for (Field field : fields) {
        allFields.add(field);
      }
      
      targetClass = targetClass.getSuperclass();
      
  } while (targetClass != null && targetClass != Object.class);
    
    return allFields;
  }
  
  public static Field findField(String name, Class<?> clazz) {
    for (Field field : getAllDeclaredFields(clazz)) {
      if (field.getName().equals(name)) {
        return field;
      }
    }
    return null;
  }
  
  public static void setField(String name, Object value, Object target) throws Exception {
    Field field = findField(name, target.getClass());
    if (field == null) {
      throw new NoSuchFieldException(name);
    }
    field.setAccessible(true);
    field.set(target, value);
  }

  public static Object value(Object obj, String fieldName) throws Exception {

    Field field = obj.getClass().getDeclaredField(fieldName);

    field.setAccessible(true);

    return field.get(obj);
  }
}
