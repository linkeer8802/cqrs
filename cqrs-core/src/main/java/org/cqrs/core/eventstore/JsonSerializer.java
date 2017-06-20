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

package org.cqrs.core.eventstore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonSerializer<T> implements Serializer<T, String> {

  static Gson gson;
  
  static {
    gson = new GsonBuilder().create();
  }
  
  @Override
  public String serialize(T object) {
    JsonObject<T> jsonObject = new JsonObject<T>(object);
    return gson.toJson(jsonObject);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T deserialize(String data) {
    JsonObject<T> jsonObject = gson.fromJson(data, new TypeToken<JsonObject<T>>(){}.getType());
    try {
      return (T) gson.fromJson(jsonObject.data.toString(), Class.forName(jsonObject.type));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
  
  class JsonObject<D> {
    public final String type;
    public final D data;
    
    public JsonObject(D data) {
      super();
      this.type = data.getClass().getName();
      this.data = data;
    }
  }
}
