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

/**
 * A MongoDB implementation of {@link com.gigaspaces.datasource.SpaceDataSource}
 *
 * @author Shadi Massalha
 */
public class MongoSpaceDataSource extends SpaceDataSource {

	private static final Log logger = LogFactory.getLog(MongoSpaceDataSource.class);

	private final MongoClientConnector mongoClient;

	public MongoSpaceDataSource(MongoClientConnector mongoClient) {

		if (mongoClient == null)
			throw new IllegalArgumentException("Argument cannot be null - mongoClient");
		this.mongoClient = mongoClient;
	}

    public void close() throws IOException {

        if (logger.isDebugEnabled())
            logger.debug("MongoSpaceDataSource.close()");

        mongoClient.close();
    }

    /**
     * Inheritance is not supported.
     */
    @Override
    public boolean supportsInheritance() {
        return false;
    }

    @Override
    public DataIterator<SpaceTypeDescriptor> initialMetadataLoad() {

        if (logger.isDebugEnabled())
            logger.debug("MongoSpaceDataSource.initialMetadataLoad()");

        Collection<SpaceTypeDescriptor> sortedCollection = mongoClient.loadMetadata();

        return new DataIteratorAdapter<SpaceTypeDescriptor>(sortedCollection.iterator());
    }

    @Override
    public DataIterator<Object> initialDataLoad() {

        if (logger.isDebugEnabled())
            logger.debug("MongoSpaceDataSource.initialDataLoad()");

        return new MongoInitialDataLoadIterator(mongoClient);
    }

    @Override
    public DataIterator<Object> getDataIterator(DataSourceQuery query) {

        if (logger.isDebugEnabled())
            logger.debug("MongoSpaceDataSource.getDataIterator(" + query + ")");

        return new MongoSqlQueryDataIterator(mongoClient, query);
    }

    @Override
	public Object getById(DataSourceIdQuery idQuery) {

		if (logger.isDebugEnabled())
			logger.debug("MongoSpaceDataSource.getById(" + idQuery + ")");

		SpaceDocumentMapper<Document> mapper = new AsyncSpaceDocumentMapper(idQuery.getTypeDescriptor());
		DocumentBuilder q = BuilderFactory.start().add(Constants.ID_PROPERTY, mapper.toObject(idQuery.getId()));
		MongoCollection c = mongoClient.getCollection(idQuery.getTypeDescriptor().getTypeName());
		Document result = c.findOne(q);
		return mapper.toDocument(result);
	}

	@Override
	public DataIterator<Object> getDataIteratorByIds(DataSourceIdsQuery idsQuery) {

        if (logger.isDebugEnabled())
            logger.debug("MongoSpaceDataSource.getDataIteratorByIds(" + idsQuery + ")");

		DocumentAssignable[] ors = new DocumentAssignable[idsQuery.getIds().length];
		for (int i=0 ; i < ors.length ; i++)
			ors[i] = BuilderFactory.start().add(Constants.ID_PROPERTY, idsQuery.getIds()[i]);
		Document q = QueryBuilder.or(ors);

		MongoCollection c = mongoClient.getCollection(idsQuery.getTypeDescriptor().getTypeName());
		MongoIterator<Document> results = c.find(q);
		return new DefaultMongoDataIterator(results, idsQuery.getTypeDescriptor());
	}
}
