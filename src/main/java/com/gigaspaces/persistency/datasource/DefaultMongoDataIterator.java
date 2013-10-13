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
package com.gigaspaces.persistency.datasource;

import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.metadata.DefaultMongoToPojoMapper;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class DefaultMongoDataIterator implements DataIterator<Object> {

	private DBCursor cursor;
	private DefaultMongoToPojoMapper mapper;

	public DefaultMongoDataIterator(DBCursor cursor,
			SpaceTypeDescriptor spaceTypeDescriptor) {
		if (cursor == null)
			throw new NullPointerException("mongo cursor can not be null");

		if (spaceTypeDescriptor == null)
			throw new IllegalArgumentException(
					"spaceTypeDescriptor can not be null");

		this.cursor = cursor;
		this.mapper = new DefaultMongoToPojoMapper(spaceTypeDescriptor);
	}

	public boolean hasNext() {
		return cursor.hasNext();
	}

	public Object next() {
		DBObject bson = cursor.next();

		return mapper.maps(bson);
	}

	public void remove() {
		cursor.remove();
	}

	public void close() {
		cursor.close();
	}

}
