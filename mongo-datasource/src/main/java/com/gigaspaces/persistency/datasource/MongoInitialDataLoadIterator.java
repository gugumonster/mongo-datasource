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

import java.util.Iterator;

import com.allanbank.mongodb.MongoIterator;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.MongoClientConnector;
import com.gigaspaces.persistency.metadata.SpaceDocumentMapper;
import com.gigaspaces.persistency.metadata.AsyncSpaceDocumentMapper;

/**
 * @author Shadi Massalha
 * 
 */
public class MongoInitialDataLoadIterator implements DataIterator<Object> {

	private MongoIterator<Document> currenCursor;

	private MongoClientConnector mongoClientWrapper;

	private Iterator<SpaceTypeDescriptor> types;
	private SpaceTypeDescriptor spaceTypeDescriptor;

	private SpaceDocumentMapper<Document> pojoMapper;

	public MongoInitialDataLoadIterator(MongoClientConnector client) {
		if (client == null)
			throw new IllegalArgumentException("mongo client can not be null");

		this.mongoClientWrapper = client;
		this.types = client.getSortedTypes().iterator();
		this.currenCursor = nextDataIterator();

	}

	public boolean hasNext() {

		while (currenCursor != null && !currenCursor.hasNext()) {
			currenCursor = nextDataIterator();
		}
		return currenCursor != null;
	}

	public Object next() {
		Document obj = currenCursor.next();

		Object pojo = pojoMapper.toDocument(obj);

		return pojo;
	}

	public void remove() {
		currenCursor.remove();

	}

	public void close() {
		if (currenCursor != null)
			currenCursor.close();
	}

	private MongoIterator<Document> nextDataIterator() {

		if (!types.hasNext())
			return null;

		spaceTypeDescriptor = types.next();
		this.pojoMapper = new AsyncSpaceDocumentMapper(spaceTypeDescriptor);

		MongoIterator<Document> cursor = mongoClientWrapper.getCollection(
				spaceTypeDescriptor.getTypeName()).find(BuilderFactory.start());

		return cursor;
	}
}
