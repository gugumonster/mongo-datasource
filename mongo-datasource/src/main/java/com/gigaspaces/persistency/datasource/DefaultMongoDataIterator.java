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

import com.allanbank.mongodb.MongoIterator;
import com.allanbank.mongodb.bson.Document;
import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.metadata.AsyncSpaceDocumentMapper;
import com.gigaspaces.persistency.metadata.SpaceDocumentMapper;

/**
 * @author Shadi Massalha
 *
 */
public class DefaultMongoDataIterator implements DataIterator<Object> {

	private MongoIterator<Document> iterator;

	private SpaceDocumentMapper<Document> mapper;

	public DefaultMongoDataIterator(MongoIterator<Document> iteraor,
			SpaceTypeDescriptor spaceTypeDescriptor) {
		if (iteraor == null)
			throw new NullPointerException("mongo cursor can not be null");

		if (spaceTypeDescriptor == null)
			throw new IllegalArgumentException(
					"spaceTypeDescriptor can not be null");

		this.iterator = iteraor;
		this.mapper = new AsyncSpaceDocumentMapper(spaceTypeDescriptor);
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public Object next() {
		Document bson = iterator.next();

		return mapper.toDocument(bson);
	}

	public void remove() {
		iterator.remove();
	}

	public void close() {
		iterator.close();
	}

}
