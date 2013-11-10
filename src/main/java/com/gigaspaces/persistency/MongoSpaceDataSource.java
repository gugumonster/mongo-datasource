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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoIterator;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentAssignable;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.builder.QueryBuilder;
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
import com.gigaspaces.persistency.metadata.AsyncSpaceDocumentMapper;
import com.gigaspaces.persistency.metadata.SpaceDocumentMapper;
import com.gigaspaces.persistency.metadata.SpaceDocumentMapperImpl;
//import com.gigaspaces.persistency.metadata.DefaultMongoToPojoMapper;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBCollection;
//import com.mongodb.DBCursor;
//import com.mongodb.DBObject;
//import com.mongodb.QueryBuilder;

/**
 * 
 * A MonogDB implementation of {@link com.gigaspaces.datasource.SpaceDataSource}
 * 
 * 
 * @author Shadi Massalha
 * 
 */
// TODO: check this implementation
public class MongoSpaceDataSource extends SpaceDataSource {

	private static final String _ID = "_id";

	private static final Log logger = LogFactory
			.getLog(MongoSpaceDataSource.class);

	private MongoClientWrapperV1 mongoClient;

	public MongoSpaceDataSource(MongoClientWrapperV1 mongoClient) {

		if (mongoClient == null)
			throw new IllegalArgumentException(
					"mongoClient must be set and initiated");

		this.mongoClient = mongoClient;
	}

	@Override
	public DataIterator<SpaceTypeDescriptor> initialMetadataLoad() {

		if (logger.isDebugEnabled())
			logger.debug("MongoSpaceDataSource.initialMetadataLoad()");

		Collection<SpaceTypeDescriptor> sortedCollection = mongoClient
				.loadMetadata();

		return new DataIteratorAdapter<SpaceTypeDescriptor>(
				sortedCollection.iterator());
	}

	@Override
	public Object getById(DataSourceIdQuery idQuery) {

		if (logger.isDebugEnabled())
			logger.debug("MongoSpaceDataSource.getById(" + idQuery + ")");

		SpaceDocumentMapper<Document> mapper = new AsyncSpaceDocumentMapper(
				idQuery.getTypeDescriptor());

		DocumentBuilder q = BuilderFactory.start().add(_ID,
				mapper.toObject(idQuery.getId()));
		// DBObject q = new BasicDBObject(_ID,
		// mapper.toObject(idQuery.getId()));

		MongoCollection c = mongoClient.getCollection(idQuery
				.getTypeDescriptor().getTypeName());

		Document cursor = c.findOne(q);

		return mapper.toDocument(cursor);
	}

	@Override
	public DataIterator<Object> getDataIterator(DataSourceQuery query) {

		if (logger.isDebugEnabled())
			logger.debug("MongoSpaceDataSource.getDataIterator()");

		return new MongoSqlQueryDataIterator(mongoClient, query);
	}

	@Override
	public DataIterator<Object> getDataIteratorByIds(DataSourceIdsQuery arg0) {
		
		List<DocumentAssignable> ors = new ArrayList<DocumentAssignable>();

		for (Object id : arg0.getIds()) {

			ors.add(BuilderFactory.start().add(_ID, id));

			// q.or(new BasicDBObject(_ID, id));
		}

		Document q1 = QueryBuilder.or(ors.toArray(new DocumentAssignable[0]));

		MongoCollection c = mongoClient.getCollection(arg0.getTypeDescriptor()
				.getTypeName());

		MongoIterator<Document> cursor = c.find(q1);

		return new DefaultMongoDataIterator(cursor, arg0.getTypeDescriptor());

	}

	@Override
	public DataIterator<Object> initialDataLoad() {
		return new MongoInitialDataLoadIterator(mongoClient);
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

	public void close() throws IOException {

		if (logger.isDebugEnabled())
			logger.debug("MongoSpaceDataSource.close()");

		mongoClient.close();
	}
}
