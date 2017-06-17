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

public class SnapshotData implements Serializable {

	private static final long serialVersionUID = 3783872635412878981L;
	
	private String aggregateId;
	private String aggregateName;
	private Long version;
	private byte[] data;
	
	public SnapshotData(String aggregateId, String aggregateName, Long version, byte[] data) {
		super();
		this.aggregateId = aggregateId;
		this.aggregateName = aggregateName;
		this.version = version;
		this.data = data;
	}

	public String getAggregateId() {
		return aggregateId;
	}
	
	public void setAggregateId(String aggregateId) {
		this.aggregateId = aggregateId;
	}
	
	public String getAggregateName() {
		return aggregateName;
	}
	
	public void setAggregateName(String aggregateName) {
		this.aggregateName = aggregateName;
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
