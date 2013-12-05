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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoIterator;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.datasource.DataSourceQuery;

import com.gigaspaces.persistency.MongoClientConnector;

import com.gigaspaces.persistency.error.UnSupportedQueryException;
import com.gigaspaces.persistency.metadata.AsyncSpaceDocumentMapper;
import com.gigaspaces.persistency.metadata.SpaceDocumentMapper;

/**
 * @author Shadi Massalha
 */
public class MongoSqlQueryDataIterator implements DataIterator<Object> {

	private static final Log logger = LogFactory
			.getLog(MongoSqlQueryDataIterator.class);

	private final MongoClientConnector client;
	private final DataSourceQuery query;
    private final SpaceDocumentMapper<Document> pojoMapper;
	private MongoIterator<Document> cursor;

	public MongoSqlQueryDataIterator(MongoClientConnector client, DataSourceQuery query) {
		if (client == null)
			throw new IllegalArgumentException("Argument cannot be null - client");
		if (query == null)
            throw new IllegalArgumentException("Argument cannot be null - query");
		if (!(query.supportsAsSQLQuery() || query.supportsTemplateAsDocument()))
			throw new UnSupportedQueryException("not sql query");

		this.client = client;
		this.query = query;
		this.pojoMapper = new AsyncSpaceDocumentMapper(query.getTypeDescriptor());
	}

	public boolean hasNext() {
		if (cursor == null) {
			init();
		}

		return cursor.hasNext();
	}

	public Object next() {

        return pojoMapper.toDocument(cursor.next());
	}

	private void init() {
		MongoCollection collection = client.getCollection(query.getTypeDescriptor().getTypeName());

		DocumentBuilder q = BuilderFactory.start();

		logger.debug(query);

		if (query.supportsAsSQLQuery())
			q = MongoQueryFactory.create(query);
		else if (query.supportsTemplateAsDocument())
			q = BuilderFactory.start(new AsyncSpaceDocumentMapper(query
					.getTypeDescriptor()).toDBObject(query
					.getTemplateAsDocument()));

		cursor = collection.find(q);
	}

	public void remove() {
		if (cursor != null)
			cursor.remove();
	}

	public void close() {
		if (cursor != null)
			cursor.close();
	}
}
