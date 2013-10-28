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

import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.datasource.DataSourceQuery;
import com.gigaspaces.persistency.MongoClientWrapper;
import com.gigaspaces.persistency.error.UnSupportedQueryException;
import com.gigaspaces.persistency.metadata.DefaultMongoToPojoMapper;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Shadi Massalha
 * 
 */
public class MongoSqlQueryDataIterator implements DataIterator<Object> {

	private static final Log logger = LogFactory
			.getLog(MongoSqlQueryDataIterator.class);

	private MongoClientWrapper pool;
	private DataSourceQuery query;
	private DBCursor cursor;
	private DefaultMongoToPojoMapper pojoMapper;

	public MongoSqlQueryDataIterator(MongoClientWrapper pool, DataSourceQuery query) {
		if (pool == null)
			throw new IllegalArgumentException("");

		if (query == null)
			throw new IllegalArgumentException("query can not be null");

		if (!query.supportsAsSQLQuery())
			throw new UnSupportedQueryException("not sql query");

		this.pool = pool;
		this.query = query;
		this.pojoMapper = new DefaultMongoToPojoMapper(
				query.getTypeDescriptor());
	}

	public boolean hasNext() {
		if (cursor == null) {
			init();
		}

		return cursor.hasNext();
	}

	public Object next() {

		Object result = pojoMapper.maps(cursor.next());

		return result;
	}

	private void init() {
		DBCollection collection = pool.getCollection(query.getTypeDescriptor()
				.getTypeName());

		DBObject q = null;

		logger.debug(query);

		if (query.supportsAsSQLQuery())
			q = MongoQueryFactory.create(query);

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
