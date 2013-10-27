/*******************************************************************************
 * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.gigaspaces.persistency.metadata;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DefaultPojoToMongoMapper implements
		Mapper<SpaceDocument, DBObject> {

	private SpaceTypeDescriptor spaceTypeDescriptor;

	public DefaultPojoToMongoMapper(SpaceTypeDescriptor spaceTypeDescriptor) {
		if (spaceTypeDescriptor == null)
			throw new IllegalArgumentException(
					"spaceTypeDescriptor can not be null");

		this.spaceTypeDescriptor = spaceTypeDescriptor;
	}

	public synchronized DBObject maps(SpaceDocument spaceDocument) {

		DBObject obj = _maps(spaceDocument.getProperties());

		return obj;
	}

	private DBObject _maps(Map<String, Object> properties) {
		Map<String, Object> map2 = new LinkedHashMap<String, Object>();

		for (Entry<String, Object> entry : properties.entrySet()) {
			
			if(entry.getValue() == null) continue;
			
			if (entry.getValue().getClass().isEnum()) {
				map2.put(entry.getKey(), entry.getValue().toString());
			} else if (entry.getValue() instanceof SpaceDocument) {
				Map<String, Object> m = ((SpaceDocument) entry.getValue())
						.getProperties();

				map2.put(entry.getKey(), _maps(m));
			} else {

				if (isId(entry.getKey()))
					map2.put("_id", entry.getValue());
				else
					map2.put(entry.getKey(), entry.getValue());
			}
		}

		return new BasicDBObject(map2);
	}

	private boolean isId(String value) {

		return spaceTypeDescriptor.getIdPropertyName().equals(value);
	}

}
