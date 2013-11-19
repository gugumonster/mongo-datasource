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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.persistency.cassandra.meta.mapping.SpaceTypeDescriptorHolder;
import org.openspaces.persistency.cassandra.meta.mapping.TypeHierarcyTopologySorter;

import com.allanbank.mongodb.Durability;
import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.MongoIterator;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentAssignable;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorVersionedSerializationUtils;
import com.gigaspaces.persistency.error.SpaceMongoDataSourceException;
import com.gigaspaces.persistency.error.SpaceMongoException;
import com.gigaspaces.persistency.metadata.AsyncSpaceDocumentMapper;
import com.gigaspaces.persistency.metadata.BatchUnit;
import com.gigaspaces.persistency.metadata.IndexBuilder;
import com.gigaspaces.persistency.metadata.SpaceDocumentMapper;
import com.gigaspaces.sync.AddIndexData;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.IntroduceTypeData;

/**
 * @author Shadi Massalha
 * 
 *         mongodb driver client wrapper
 */
public class MongoClientWrapper {

	private static final String ERROR_OCCURS_WHILE_TRY_DESERIALIZE_OBJECT = "error occurs while try deserialize object: ";
	private static final String ERROR_OCCUR_WHILE_SERIALIZE_AND_SAVE_TYPE_DESCRIPTOR = "error occurs while serialize and save type descriptor: ";
	private static final String TYPE_DESCRIPTOR_FIELD_NAME = "value";
	private static final String DEFAULT_ID = "_id";
	private static final String METADATA_COLLECTION_NAME = "metadata";

	private static final Log logger = LogFactory
			.getLog(MongoClientWrapper.class);

	private final MongoClient client;
	private String dbName;

	// TODO: shadi must add documentation
	private static final Map<String, SpaceTypeDescriptorHolder> types = new ConcurrentHashMap<String, SpaceTypeDescriptorHolder>();
	private static final Map<String, SpaceDocumentMapper<Document>> _mappingCache = new ConcurrentHashMap<String, SpaceDocumentMapper<Document>>();

	private IndexBuilder indexBuilder;

	public MongoClientWrapper(MongoClient client, String db) {

		this.client = client;
		this.dbName = db;
		this.indexBuilder = new IndexBuilder(this);
	}

	/**
	 * @param introduceTypeData
	 */
	public void introduceType(IntroduceTypeData introduceTypeData) {

		SpaceTypeDescriptor spaceTypeDescriptor = introduceTypeData
				.getTypeDescriptor();

		introduceType(spaceTypeDescriptor);
	}

	/**
	 * @param spaceTypeDescriptor
	 */
	public void introduceType(SpaceTypeDescriptor spaceTypeDescriptor) {
		MongoCollection m = getConnection().getCollection(
				METADATA_COLLECTION_NAME);

		DocumentBuilder builder = BuilderFactory.start();

		builder.add(DEFAULT_ID, spaceTypeDescriptor.getTypeName());

		writeMetadata(spaceTypeDescriptor, m, builder);
	}

	/**
	 * serialize the type descriptor to binary stream and save it to metadata
	 * collection
	 * 
	 * @param introduceTypeData
	 * @param spaceTypeDescriptor
	 * @param m
	 * @param builder
	 */
	private void writeMetadata(SpaceTypeDescriptor spaceTypeDescriptor,
			MongoCollection m, DocumentBuilder builder) {
		try {

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			ObjectOutputStream out = new ObjectOutputStream(bos);

			IOUtils.writeObject(out,
					SpaceTypeDescriptorVersionedSerializationUtils
							.toSerializableForm(spaceTypeDescriptor));

			builder.add(TYPE_DESCRIPTOR_FIELD_NAME, bos.toByteArray());

			int wr = m.save(builder);

			if (logger.isTraceEnabled())
				logger.trace(wr);

			indexBuilder.ensureIndexes(spaceTypeDescriptor);

		} catch (IOException e) {
			logger.error(e);

			throw new SpaceMongoException(
					ERROR_OCCUR_WHILE_SERIALIZE_AND_SAVE_TYPE_DESCRIPTOR
							+ spaceTypeDescriptor, e);
		}
	}

	/**
	 * @return mongodb DB object
	 */
	public MongoDatabase getConnection() {
		MongoDatabase db = client.getDatabase(dbName);

		// if (StringUtils.hasLength(user))
		// db.authenticate(user, password);

		return db;
	}

	/**
	 * @param collectionName
	 *            - name of the requested mongodb collection
	 * @return
	 */
	public MongoCollection getCollection(String collectionName) {

		MongoDatabase db = getConnection();

		return db.getCollection(collectionName);
	}

	/**
	 * @param rows
	 *            - batch units which includes space documents and target
	 *            operation type to be performed
	 */
	public void performBatch(List<BatchUnit> rows) {
		if (logger.isTraceEnabled()) {
			logger.trace("MongoClientWrapper.performBatch(" + rows + ")");
			logger.trace("Batch size to be performed is " + rows.size());
		}

		int length = rows.size();

		List<Future<Integer>> saveReplies = new ArrayList<Future<Integer>>();
		List<Future<Long>> updatesReplies = new ArrayList<Future<Long>>();
		List<Future<Long>> deltedReplies = new ArrayList<Future<Long>>();

		for (int i = 0; i < length; i++) {
			BatchUnit batchUnit = rows.get(i);

			SpaceDocument spaceDoc = batchUnit.getSpaceDocument();
			SpaceTypeDescriptorHolder spaceTypeDescriptor = types.get(batchUnit
					.getTypeName());

			SpaceDocumentMapper<Document> mapper = getMapper(spaceTypeDescriptor
					.getTypeDescriptor());

			DocumentAssignable obj = mapper.toDBObject(spaceDoc);

			MongoCollection col = getCollection(batchUnit.getTypeName());

			switch (batchUnit.getDataSyncOperationType()) {

			case WRITE:
			case UPDATE:
				Future<Integer> future = col.saveAsync(obj, Durability.ACK);
				saveReplies.add(future);
				break;
			case PARTIAL_UPDATE: // TODO: add partial update and change api
									// support and wiki documentaion
			case CHANGE:// TODO: add partial update and change api support and
						// wiki documentaion

				Document query = BuilderFactory
						.start()
						.add("_id",
								((Document) obj).get("_id").getValueAsObject())
						.build();

				Future<Long> updateFuture = col.updateAsync(query,
						removeNulls((Document) obj), Durability.ACK);
				updatesReplies.add(updateFuture);

				break;
			// case REMOVE_BY_UID: // TODO: not supported by cassandra
			// implementation
			case REMOVE:
				deltedReplies.add(col.deleteAsync(obj, false, Durability.ACK));
				break;
			default:
				throw new IllegalStateException(
						"Unsupported data sync operation type: "
								+ batchUnit.getDataSyncOperationType());

			}
		}

		long totalCount = waitForSave(saveReplies) + waitFor(updatesReplies)
				+ waitFor(deltedReplies);

		if (logger.isTraceEnabled()) {
			logger.trace("total accepted replies is: " + totalCount);
		}
	}

	private long waitForSave(List<Future<Integer>> replies) {

		long total = 0;

		for (Future<Integer> future : replies) {
			try {
				total += future.get().intValue();
			} catch (InterruptedException e) {
				throw new SpaceMongoException("Asynchronize repolies size is: "
						+ replies.size(), e);
			} catch (ExecutionException e) {
				throw new SpaceMongoException("Asynchronize repolies size is: "
						+ replies.size(), e);
			}
		}

		return (total < 0) ? -1 * total : total;
	}

	public long waitFor(List<Future<Long>> replies) {

		long total = 0;

		for (Future<Long> future : replies) {
			try {
				total += future.get().longValue();
			} catch (InterruptedException e) {
				throw new SpaceMongoException("Asynchronize repolies size is: "
						+ replies.size(), e);
			} catch (ExecutionException e) {
				throw new SpaceMongoException("Asynchronize repolies size is: "
						+ replies.size(), e);
			}
		}

		return (total < 0) ? -1 * total : total;
	}

	private Document removeNulls(Document obj) {

		DocumentBuilder builder = BuilderFactory.start();

		for (Element e : obj.getElements()) {

			String key = e.getName();

			if ("_id".equals(key))
				continue;

			Object value = obj.get(key).getValueAsObject();

			if (value == null)
				continue;

			if (value instanceof DocumentAssignable) {
				builder.push("$set").add(key, removeNulls(obj));
			} else
				builder.add(key, value);
		}

		return builder.build();
	}

	protected SpaceDocumentMapper<Document> getMapper(
			SpaceTypeDescriptor spaceTypeDescriptor) {

		SpaceDocumentMapper<Document> mapper = _mappingCache
				.get(spaceTypeDescriptor.getTypeName());

		if (mapper == null) {
			mapper = new AsyncSpaceDocumentMapper(spaceTypeDescriptor);
			_mappingCache.put(spaceTypeDescriptor.getTypeName(), mapper);
		}

		return mapper;
	}

	public void cacheSpaceTypeDesciptor(SpaceTypeDescriptor spaceTypeDescriptor) {

		if (spaceTypeDescriptor == null)
			throw new IllegalArgumentException(
					"spaceTypeDescriptor can not be null");

		if (!types.containsKey(spaceTypeDescriptor.getTypeName())) {
			introduceType(spaceTypeDescriptor);
		}

		SpaceTypeDescriptorHolder holder = new SpaceTypeDescriptorHolder(
				spaceTypeDescriptor);

		types.put(spaceTypeDescriptor.getTypeName(), holder);
	}

	public void close() throws IOException {

		client.close();
	}

	public Collection<SpaceTypeDescriptor> loadMetadata() {

		MongoCollection metadata = getConnection().getCollection(
				METADATA_COLLECTION_NAME);

		MongoIterator<Document> cursor = metadata.find(BuilderFactory.start()
				.build());

		while (cursor.hasNext()) {

			Document type = cursor.next();

			Object b = type.get(TYPE_DESCRIPTOR_FIELD_NAME).getValueAsObject();

			readMetadata(b);
		}

		return getSortedTypes();
	}

	/**
	 * read object as byte array of type {@link SpaceTypeDescriptor} then ensure
	 * indexes and put it in types cache
	 * 
	 * @param b
	 *            - object to be casted to type array
	 */
	private void readMetadata(Object b) {
		try {

			ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
					(byte[]) b));

			Serializable typeDescriptorVersionedSerializableWrapper = IOUtils
					.readObject(in);

			SpaceTypeDescriptor spaceTypeDescriptor = SpaceTypeDescriptorVersionedSerializationUtils
					.fromSerializableForm(typeDescriptorVersionedSerializableWrapper);

			indexBuilder.ensureIndexes(spaceTypeDescriptor);

			cacheSpaceTypeDesciptor(spaceTypeDescriptor);

		} catch (ClassNotFoundException e) {
			logger.error(e);
			throw new SpaceMongoDataSourceException(
					ERROR_OCCURS_WHILE_TRY_DESERIALIZE_OBJECT + b, e);
		} catch (IOException e) {
			logger.error(e);
			throw new SpaceMongoDataSourceException(
					ERROR_OCCURS_WHILE_TRY_DESERIALIZE_OBJECT + b, e);
		}
	}

	/**
	 * Encapsulate {@link DataSyncOperation} into batch helper POJO and create
	 * new batch list
	 * 
	 * @param dataSyncOperations
	 */
	public void performBatch(DataSyncOperation[] dataSyncOperations) {
		int length = dataSyncOperations.length;

		List<BatchUnit> rows = new LinkedList<BatchUnit>();

		for (int index = 0; index < length; index++) {

			BatchUnit bu = new BatchUnit();
			DataSyncOperation dso = dataSyncOperations[index];

			cacheSpaceTypeDesciptor(dso.getTypeDescriptor());

			bu.setSpaceDocument(dso.getDataAsDocument());
			bu.setDataSyncOperationType(dso.getDataSyncOperationType());

			rows.add(bu);
		}

		performBatch(rows);
	}

	/**
	 * @return - returned sorted list regard of inheritance hierarchy supper
	 *         class ascending
	 */
	public Collection<SpaceTypeDescriptor> getSortedTypes() {

		return TypeHierarcyTopologySorter.getSortedList(types);
	}

	public void ensureIndexes(AddIndexData addIndexData) {
		indexBuilder.ensureIndexes(addIndexData);
	}
}
