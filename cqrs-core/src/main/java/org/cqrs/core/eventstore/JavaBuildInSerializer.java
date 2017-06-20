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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JavaBuildInSerializer<T> implements Serializer<T, byte[]> {

	@Override
	public byte[] serialize(T object){
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			
			bos.close();
			oos.close();
			
			return bos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("serialize object fail.", e);
		}
	}

	@SuppressWarnings("unchecked")
  @Override
	public T deserialize(byte[] data) {
		
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bis);
			
			bis.close();
			ois.close();
			
			return (T) ois.readObject();
		} catch (Exception e) {
			throw new RuntimeException("deserialize object fail.", e);
		}
	}
}
