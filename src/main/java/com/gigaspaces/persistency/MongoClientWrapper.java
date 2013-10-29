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
import org.springframework.util.StringUtils;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.metadata.BatchUnit;
import com.gigaspaces.persistency.metadata.DefaultPojoToMongoMapper;
import com.gigaspaces.persistency.metadata.Mapper;
import com.gigaspaces.persistency.metadata.MetadataManager;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * @author Shadi Massalha
 * 
 */
public class MongoClientWrapper {

	private static final Log logger = LogFactory
			.getLog(MongoClientWrapper.class);

	private final MongoClient client;
	private String dbName;

	// TODO: shadi must add documentation
	private static final Map<String, SpaceTypeDescriptorHolder> types = new HashMap<String, SpaceTypeDescriptorHolder>();
	private static final Map<String, Mapper<SpaceDocument, DBObject>> _mappingCache = new HashMap<String, Mapper<SpaceDocument, DBObject>>();

	private final Object batchSynchLock = new Object();

	private char[] password = new char[0];
	private String user;

	private static final Object synch = new Object();

	public MongoClientWrapper(MongoClient client, String db) {
		this.client = client;
		this.dbName = db;

	}

	public MongoClientWrapper(MongoClient client2, String db, String user,
			String password) {
		this(client2, db);

		if (user != null)
			this.user = user;

		if (password != null)
			this.password = password.toCharArray();
	}

	public synchronized DB getConnection() {
		DB db = client.getDB(dbName);
		
		if (StringUtils.hasLength(user))
			db.authenticate(user, password);
		
		return db;
	}

	/**
	 * @param collectionName
	 *            - name of the requested mongodb collection
	 * @return
	 */
	public synchronized DBCollection getCollection(String collectionName) {

		DB db = getConnection();

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
		// TODO: check conccurency
		// synchronized (batchSynchLock) {

		for (int i = 0; i < length; i++) {
			BatchUnit batchUnit = rows.get(i);

			SpaceDocument spaceDoc = batchUnit.getSpaceDocument();
			SpaceTypeDescriptorHolder spaceTypeDescriptor = types.get(batchUnit
					.getTypeName());

			Mapper<SpaceDocument, DBObject> mapper = getMapper(spaceTypeDescriptor
					.getTypeDescriptor());
			String id = spaceTypeDescriptor.getTypeDescriptor()
					.getIdPropertyName();

			DBObject obj = mapper.maps(spaceDoc);

			DBCollection col = getCollection(batchUnit.getTypeName());

			switch (batchUnit.getDataSyncOperationType()) {

			case WRITE:
			case UPDATE:
			case PARTIAL_UPDATE: // TODO: add partial update and change api
									// support and wiki documentaion
			case CHANGE:// TODO: add partial update and change api support and
						// wiki documentaion
				col.save(obj);
				break;
			// case REMOVE_BY_UID: // TODO: not supported by cassandra
			// implementation
			case REMOVE:
				col.remove(obj);
				break;
			default:
				throw new IllegalStateException(
						"Unsupported data sync operation type: "
								+ batchUnit.getDataSyncOperationType());

			}
		}
		// }
	}

	protected Mapper<SpaceDocument, DBObject> getMapper(
			SpaceTypeDescriptor spaceTypeDescriptor) {

		Mapper<SpaceDocument, DBObject> mapper = null;
		// TODO: change to conccurency hash map
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
			SpaceTypeDescriptor spaceTypeDescriptor,
			MetadataManager metadataManager) {

		if (spaceTypeDescriptor == null)
			throw new IllegalArgumentException(
					"spaceTypeDescriptor can not be null");

		if (!types.containsKey(spaceTypeDescriptor.getTypeName())) {
			metadataManager.introduceType(spaceTypeDescriptor);
		}

		SpaceTypeDescriptorHolder holder = new SpaceTypeDescriptorHolder(
				spaceTypeDescriptor);

		types.put(spaceTypeDescriptor.getTypeName(), holder);
	}

	public synchronized void close() {

		client.close();
	}

	public synchronized SpaceTypeDescriptorHolder getSpaceTypeDescriptor(
			String typeName) {

		return types.get(typeName);
	}

	public synchronized Map<String, SpaceTypeDescriptorHolder> getTypes() {

		return types;
	}
}
