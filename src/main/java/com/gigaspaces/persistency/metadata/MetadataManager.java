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
package com.gigaspaces.persistency.metadata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.persistency.cassandra.meta.mapping.TypeHierarcyTopologySorter;

import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorVersionedSerializationUtils;
import com.gigaspaces.persistency.MongoClientWrapper;
import com.gigaspaces.persistency.MongoSpaceSynchronizationEndpoint;
import com.gigaspaces.persistency.error.SpaceMongoDataSourceException;
import com.gigaspaces.sync.AddIndexData;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.IntroduceTypeData;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * @author Shadi Massalha
 * 
 * 
 */
public class MetadataManager {

	private static final String TYPE_DESCRIPTOR_FIELD_NAME = "value";
	private static final String DEFAULT_ID = "_id";
	private static final String METADATA_COLLECTION_NAME = "metadata";

	private static final Log logger = LogFactory
			.getLog(MongoSpaceSynchronizationEndpoint.class);

	private final MongoClientWrapper pool;
	private IndexBuilder indexBuilder;

	public MetadataManager(MongoClientWrapper pool) {

		if (pool == null)
			throw new IllegalArgumentException("mongo client can not be null");

		this.pool = pool;

		this.indexBuilder = new IndexBuilder(pool);
	}

	public void introduceType(IntroduceTypeData introduceTypeData) {

		SpaceTypeDescriptor spaceTypeDescriptor = introduceTypeData
				.getTypeDescriptor();

		introduceType(spaceTypeDescriptor);
	}

	public void introduceType(SpaceTypeDescriptor spaceTypeDescriptor) {
		DBCollection m = getPool().getCollection(METADATA_COLLECTION_NAME);

		BasicDBObject obj = new BasicDBObject();

		obj.append(DEFAULT_ID, spaceTypeDescriptor.getTypeName());

		writeMetadata(spaceTypeDescriptor, m, obj);
	}

	/**
	 * serialize the type descriptor to binary stream and save it to metadata
	 * collection
	 * 
	 * @param introduceTypeData
	 * @param spaceTypeDescriptor
	 * @param m
	 * @param obj
	 */
	private void writeMetadata(SpaceTypeDescriptor spaceTypeDescriptor,
			DBCollection m, BasicDBObject obj) {
		try {

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			ObjectOutputStream out = new ObjectOutputStream(bos);

			IOUtils.writeObject(out,
					SpaceTypeDescriptorVersionedSerializationUtils
							.toSerializableForm(spaceTypeDescriptor));

			obj.append(TYPE_DESCRIPTOR_FIELD_NAME, bos.toByteArray());

			WriteResult wr = m.save(obj, WriteConcern.SAFE);

			logger.trace(wr);

			indexBuilder.ensureIndexes(spaceTypeDescriptor);

			//pool.cacheSpaceTypeDesciptor(spaceTypeDescriptor, this);

		} catch (IOException e) {
			logger.error(e);
			//TODO: throw exception shadi
		}
	}

	public void ensureIndexes(AddIndexData addIndexData) {
		indexBuilder.ensureIndexes(addIndexData);
	}

	public MongoClientWrapper getPool() {
		return pool;
	}

	public void performBatch(DataSyncOperation[] dataSyncOperations) {
		int length = dataSyncOperations.length;

		List<BatchUnit> rows = new LinkedList<BatchUnit>();

		for (int index = 0; index < length; index++) {

			BatchUnit bu = new BatchUnit();
			DataSyncOperation dso = dataSyncOperations[index];

			pool.cacheSpaceTypeDesciptor(dso.getTypeDescriptor(), this);

			bu.setSpaceDocument(dso.getDataAsDocument());
			bu.setDataSyncOperationType(dso.getDataSyncOperationType());

			rows.add(bu);
		}

		pool.performBatch(rows);

	}

	public void close() {
		pool.close();

	}

	public Collection<SpaceTypeDescriptor> loadMetadata() {

		DBCollection metadata = pool.getCollection(METADATA_COLLECTION_NAME);

		DBCursor cursor = metadata.find();

		while (cursor.hasNext()) {

			DBObject type = cursor.next();

			Object b = type.get(TYPE_DESCRIPTOR_FIELD_NAME);

			readMetadata(b);
		}

		return getTypes();
	}

	private void readMetadata(Object b) {
		try {

			ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
					(byte[]) b));

			Serializable typeDescriptorVersionedSerializableWrapper = IOUtils
					.readObject(in);

			SpaceTypeDescriptor spaceTypeDescriptor = SpaceTypeDescriptorVersionedSerializationUtils
					.fromSerializableForm(typeDescriptorVersionedSerializableWrapper);

			indexBuilder.ensureIndexes(spaceTypeDescriptor);

			pool.cacheSpaceTypeDesciptor(spaceTypeDescriptor, this);

		} catch (ClassNotFoundException e) {
			logger.error(e);
			throw new SpaceMongoDataSourceException("", e);
		} catch (IOException e) {
			logger.error(e);
			throw new SpaceMongoDataSourceException("", e);
		}

	}

	public synchronized Collection<SpaceTypeDescriptor> getTypes() { 
		
		List<SpaceTypeDescriptor> result = TypeHierarcyTopologySorter
				.getSortedList(pool.getTypes());
		return result;
	}
}
