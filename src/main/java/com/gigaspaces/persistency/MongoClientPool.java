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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.persistency.cassandra.meta.mapping.SpaceTypeDescriptorHolder;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.metadata.BatchUnit;
import com.gigaspaces.persistency.metadata.DefaultPojoToMongoMapper;
import com.gigaspaces.persistency.metadata.Mapper;
import com.gigaspaces.sync.DataSyncOperation;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * @author Shadi Massalha
 * 
 */
public class MongoClientPool {

	private static final Log logger = LogFactory.getLog(MongoClientPool.class);

	private final MongoClient client;
	private String dbName;

	private static final Map<String, SpaceTypeDescriptorHolder> types = new HashMap<String, SpaceTypeDescriptorHolder>();
	private static final Map<String, Mapper<SpaceDocument, DBObject>> _mappingCache = new HashMap<String, Mapper<SpaceDocument, DBObject>>();

	private final Object batchSynchLock = new Object();
	private static final Object synch = new Object();

	public MongoClientPool(ServerAddress host, String db) {
		this.client = new MongoClient(host);
		this.dbName = db;
			
	}

	public synchronized DB checkOut() {
		DB db = client.getDB(dbName);
		return db;
	}

	/**
	 * @param collectionName
	 *            - name of the requested mongodb collection
	 * @return
	 */
	public synchronized DBCollection getCollection(String collectionName) {

		DB db = checkOut();

		return db.getCollection(collectionName);
	}

	/**
	 * @param rows
	 *            - batch units which includes space documents and target
	 *            operation type to be performed
	 */
	public void performBatch(List<BatchUnit> rows) {
		if (logger.isTraceEnabled()) {
			logger.trace("MongoClientPool.performBatch(" + rows + ")");
			logger.trace("Batch size to be performed is " + rows.size());
		}

		int length = rows.size();

		synchronized (batchSynchLock) {

			for (int i = 0; i < length; i++) {
				BatchUnit batchUnit = rows.get(i);

				SpaceDocument spaceDoc = batchUnit.getSpaceDocument();
				SpaceTypeDescriptorHolder spaceTypeDescriptor = types
						.get(batchUnit.getTypeName());


				Mapper<SpaceDocument, DBObject> mapper = getMapper(spaceTypeDescriptor
						.getTypeDescriptor());

				DBObject obj = mapper.maps(spaceDoc);

				DBCollection col = getCollection(batchUnit.getTypeName());

				switch (batchUnit.getDataSyncOperationType()) {
				case WRITE:
				case UPDATE:
				case PARTIAL_UPDATE:
				case CHANGE:
					col.save(obj);
					break;
				case REMOVE:
					col.remove(obj);
					break;
				default:
					throw new IllegalStateException(
							"Unsupported data sync operation type: "
									+ batchUnit.getDataSyncOperationType());

				}
			}
		}
	}

	public void performBatch(DataSyncOperation[] dataSyncOperations) {

		if (logger.isTraceEnabled()) {
			logger.trace("MongoClientPool.performBatch(" + dataSyncOperations
					+ ")");
			logger.trace("Batch size to be performed is "
					+ dataSyncOperations.length);
		}

		synchronized (batchSynchLock) {
			int len = dataSyncOperations.length;

			for (int i = 0; i < len; i++) {
				DataSyncOperation dataSyncOperation = dataSyncOperations[i];

				if (!dataSyncOperation.supportsDataAsDocument())
					continue;

				SpaceDocument spaceDoc = dataSyncOperation.getDataAsDocument();
				SpaceTypeDescriptor spaceTypeDescriptor = dataSyncOperation
						.getTypeDescriptor();

				Mapper<SpaceDocument, DBObject> mapper = getMapper(spaceTypeDescriptor);

				DBObject obj = mapper.maps(spaceDoc);

				DBCollection col = getCollection(spaceTypeDescriptor
						.getTypeName());

				switch (dataSyncOperation.getDataSyncOperationType()) {
				case WRITE:
				case UPDATE:
				case PARTIAL_UPDATE:
				case CHANGE:
					col.save(obj);
					break;
				case REMOVE:
					col.remove(obj);
					break;
				default:
					throw new IllegalStateException(
							"Unsupported data sync operation type: "
									+ dataSyncOperation
											.getDataSyncOperationType());

				}
			}
		}
	}

	protected Mapper<SpaceDocument, DBObject> getMapper(
			SpaceTypeDescriptor spaceTypeDescriptor) {

		Mapper<SpaceDocument, DBObject> mapper = null;

		synchronized (synch) {
			mapper = _mappingCache.get(spaceTypeDescriptor.getTypeName());

			if (mapper == null) {
				mapper = new DefaultPojoToMongoMapper(spaceTypeDescriptor);

				_mappingCache.put(spaceTypeDescriptor.getTypeName(), mapper);
			}

		}

		return mapper;
	}

	public synchronized void cacheSpaceTypeDesciptor(
			SpaceTypeDescriptor spaceTypeDescriptor) {

		if (spaceTypeDescriptor == null)
			throw new IllegalArgumentException(
					"spaceTypeDescriptor can not be null");

		types.put(spaceTypeDescriptor.getTypeName(),
				new SpaceTypeDescriptorHolder(spaceTypeDescriptor));
	}

	public synchronized void close() {

		client.close();
	}

	public synchronized SpaceTypeDescriptorHolder getSpaceTypeDescriptor(
			String typeName) {

		return types.get(typeName);
	}

	public synchronized Map<String,SpaceTypeDescriptorHolder> getTypes() {

		return types;
	}
}
