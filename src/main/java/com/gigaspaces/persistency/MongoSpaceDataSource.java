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
package com.gigaspaces.persistency;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.datasource.DataIteratorAdapter;
import com.gigaspaces.datasource.DataSourceIdQuery;
import com.gigaspaces.datasource.DataSourceIdsQuery;
import com.gigaspaces.datasource.DataSourceQuery;
import com.gigaspaces.datasource.SpaceDataSource;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.datasource.DefaultMongoDataIterator;
import com.gigaspaces.persistency.datasource.MongoInitialDataLoadIterator;
import com.gigaspaces.persistency.datasource.MongoSqlQueryDataIterator;
import com.gigaspaces.persistency.metadata.DefaultMongoToPojoMapper;
import com.gigaspaces.persistency.metadata.MetadataManager;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

/**
 * 
 * A MonogDB implementation of {@link com.gigaspaces.datasource.SpaceDataSource}
 * 
 * 
 * @author Shadi Massalha
 */
public class MongoSpaceDataSource extends SpaceDataSource {

	private static final Log logger = LogFactory
			.getLog(MongoSpaceDataSource.class);

	private MongoClientWrapper mongoClient;

	private final MetadataManager metadataManager;

	public MongoSpaceDataSource(MongoClientWrapper mongoClient) {

		if (mongoClient == null)
			throw new IllegalArgumentException(
					"mongoClient must be set and initiated");

		this.metadataManager = new MetadataManager(mongoClient);
		this.mongoClient = mongoClient;
	}

	@Override
	public DataIterator<SpaceTypeDescriptor> initialMetadataLoad() {

		if (logger.isDebugEnabled())
			logger.debug("MongoSpaceDataSource.initialMetadataLoad()");

		metadataManager.loadMetadata();

		return new DataIteratorAdapter<SpaceTypeDescriptor>(metadataManager
				.getTypes().iterator());
	}

	@Override
	public Object getById(DataSourceIdQuery idQuery) {

		if (logger.isDebugEnabled())
			logger.debug("MongoSpaceDataSource.getById(" + idQuery + ")");

		DBObject q = new BasicDBObject("_id", idQuery.getId());

		DBCollection c = mongoClient.getCollection(idQuery.getTypeDescriptor()
				.getTypeName());

		DBObject cursor = c.findOne(q);

		DefaultMongoToPojoMapper mapper = new DefaultMongoToPojoMapper(
				idQuery.getTypeDescriptor());

		return mapper.maps(cursor);
	}

	@Override
	public DataIterator<Object> getDataIterator(DataSourceQuery query) {

		if (logger.isDebugEnabled())
			logger.debug("MongoSpaceDataSource.getDataIterator()");

		return new MongoSqlQueryDataIterator(mongoClient, query);
	}

	@Override
	public DataIterator<Object> getDataIteratorByIds(DataSourceIdsQuery arg0) {

		QueryBuilder q = QueryBuilder.start();

		for (Object id : arg0.getIds()) {

			q.or(new BasicDBObject("_id", id));
		}

		DBObject q1 = q.get();

		DBCollection c = mongoClient.getCollection(arg0.getTypeDescriptor()
				.getTypeName());

		DBCursor cursor = c.find(q1);

		return new DefaultMongoDataIterator(cursor, arg0.getTypeDescriptor());

	}

	@Override
	public DataIterator<Object> initialDataLoad() {
		return new MongoInitialDataLoadIterator(metadataManager.getTypes(),
				mongoClient);
	}

	/**
	 * Returns <code>false</code>, inheritance is not supported.
	 * 
	 * @return <code>false</code>.
	 */
	@Override
	public boolean supportsInheritance() {
		return false;
	}

	public void close() {

		if (logger.isDebugEnabled())
			logger.debug("MongoSpaceDataSource.close()");

		metadataManager.close();
	}
}
