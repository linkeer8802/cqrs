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

import java.io.Serializable;

public class EventData implements Serializable {
	
	private static final long serialVersionUID = 171154851792202615L;
	
	private String aggregateId;
	private Long timestamp;
	private Long version;
	private byte[] data;
	
	public EventData(String aggregateId, Long timestamp, Long version, byte[] data) {
		super();
		this.aggregateId = aggregateId;
		this.timestamp = timestamp;
		this.version = version;
		this.data = data;
	}

	public String getAggregateId() {
		return aggregateId;
	}
	
	public void setAggregateId(String aggregateId) {
		this.aggregateId = aggregateId;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Long getVersion() {
		return version;
	}
	
	public void setVersion(Long version) {
		this.version = version;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
}
