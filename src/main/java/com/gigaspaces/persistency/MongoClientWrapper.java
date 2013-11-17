///*******************************************************************************
// * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *******************************************************************************/
//package com.gigaspaces.persistency;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.ObjectInput;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.Serializable;
//import java.util.Collection;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.openspaces.persistency.cassandra.meta.mapping.SpaceTypeDescriptorHolder;
//import org.openspaces.persistency.cassandra.meta.mapping.TypeHierarcyTopologySorter;
//import org.springframework.util.StringUtils;
//
//import com.gigaspaces.document.SpaceDocument;
//import com.gigaspaces.internal.io.IOUtils;
//import com.gigaspaces.metadata.SpaceTypeDescriptor;
//import com.gigaspaces.metadata.SpaceTypeDescriptorVersionedSerializationUtils;
//import com.gigaspaces.persistency.error.SpaceMongoDataSourceException;
//import com.gigaspaces.persistency.error.SpaceMongoException;
//import com.gigaspaces.persistency.metadata.BatchUnit;
//import com.gigaspaces.persistency.metadata.IndexBuilder;
//import com.gigaspaces.persistency.metadata.SpaceDocumentMapper;
//import com.gigaspaces.persistency.metadata.SpaceDocumentMapperImpl;
//import com.gigaspaces.sync.AddIndexData;
//import com.gigaspaces.sync.DataSyncOperation;
//import com.gigaspaces.sync.IntroduceTypeData;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.DBCursor;
//import com.mongodb.DBObject;
//import com.mongodb.MongoClient;
//import com.mongodb.WriteConcern;
//import com.mongodb.WriteResult;
//
///**
// * @author Shadi Massalha
// * 
// *         mongodb driver client wrapper
// */
//// TODO: check this implementation
//public class MongoClientWrapper {
//
//	private static final String ERROR_OCCURS_WHILE_TRY_DESERIALIZE_OBJECT = "error occurs while try deserialize object: ";
//	private static final String ERROR_OCCUR_WHILE_SERIALIZE_AND_SAVE_TYPE_DESCRIPTOR = "error occurs while serialize and save type descriptor: ";
//	private static final String TYPE_DESCRIPTOR_FIELD_NAME = "value";
//	private static final String DEFAULT_ID = "_id";
//	private static final String METADATA_COLLECTION_NAME = "metadata";
//
//	private static final Log logger = LogFactory
//			.getLog(MongoClientWrapper.class);
//
//	private final MongoClient client;
//	private String dbName;
//
//	// TODO: shadi must add documentation
//	private static final Map<String, SpaceTypeDescriptorHolder> types = new ConcurrentHashMap<String, SpaceTypeDescriptorHolder>();
//	private static final Map<String, SpaceDocumentMapper<DBObject>> _mappingCache = new ConcurrentHashMap<String, SpaceDocumentMapper<DBObject>>();
//
//	private char[] password = new char[0];
//	private String user;
//	private IndexBuilder indexBuilder;
//
//	public MongoClientWrapper(MongoClient client, String db) {
//
//		this.client = client;
//		this.dbName = db;
//		//this.indexBuilder = new IndexBuilder(this);
//	}
//
//	public MongoClientWrapper(MongoClient client, String db, String user,
//			String password) {
//		this(client, db);
//
//		if (user != null)
//			this.user = user;
//
//		if (password != null)
//			this.password = password.toCharArray();
//	}
//
//	/**
//	 * @param introduceTypeData
//	 */
//	public void introduceType(IntroduceTypeData introduceTypeData) {
//
//		SpaceTypeDescriptor spaceTypeDescriptor = introduceTypeData
//				.getTypeDescriptor();
//
//		introduceType(spaceTypeDescriptor);
//	}
//
//	/**
//	 * @param spaceTypeDescriptor
//	 */
//	public void introduceType(SpaceTypeDescriptor spaceTypeDescriptor) {
//		DBCollection m = getConnection()
//				.getCollection(METADATA_COLLECTION_NAME);
//
//		BasicDBObject obj = new BasicDBObject();
//
//		obj.append(DEFAULT_ID, spaceTypeDescriptor.getTypeName());
//
//		writeMetadata(spaceTypeDescriptor, m, obj);
//	}
//
//	/**
//	 * serialize the type descriptor to binary stream and save it to metadata
//	 * collection
//	 * 
//	 * @param introduceTypeData
//	 * @param spaceTypeDescriptor
//	 * @param m
//	 * @param obj
//	 */
//	private void writeMetadata(SpaceTypeDescriptor spaceTypeDescriptor,
//			DBCollection m, BasicDBObject obj) {
//		try {
//
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//			ObjectOutputStream out = new ObjectOutputStream(bos);
//
//			IOUtils.writeObject(out,
//					SpaceTypeDescriptorVersionedSerializationUtils
//							.toSerializableForm(spaceTypeDescriptor));
//
//			obj.append(TYPE_DESCRIPTOR_FIELD_NAME, bos.toByteArray());
//
//			WriteResult wr = m.save(obj, WriteConcern.SAFE);
//
//			if (logger.isTraceEnabled())
//				logger.trace(wr);
//
//			indexBuilder.ensureIndexes(spaceTypeDescriptor);
//
//		} catch (IOException e) {
//			logger.error(e);
//
//			throw new SpaceMongoException(
//					ERROR_OCCUR_WHILE_SERIALIZE_AND_SAVE_TYPE_DESCRIPTOR
//							+ spaceTypeDescriptor, e);
//		}
//	}
//
//	/**
//	 * @return mongodb DB object
//	 */
//	public DB getConnection() {
//		DB db = client.getDB(dbName);
//
//		if (StringUtils.hasLength(user))
//			db.authenticate(user, password);
//
//		return db;
//	}
//
//	/**
//	 * @param collectionName
//	 *            - name of the requested mongodb collection
//	 * @return
//	 */
//	public DBCollection getCollection(String collectionName) {
//
//		DB db = getConnection();
//
//		return db.getCollection(collectionName);
//	}
//
//	/**
//	 * @param rows
//	 *            - batch units which includes space documents and target
//	 *            operation type to be performed
//	 */
//	public void performBatch(List<BatchUnit> rows) {
//		if (logger.isTraceEnabled()) {
//			logger.trace("MongoClientWrapper.performBatch(" + rows + ")");
//			logger.trace("Batch size to be performed is " + rows.size());
//		}
//
//		int length = rows.size();
//
//		for (int i = 0; i < length; i++) {
//			BatchUnit batchUnit = rows.get(i);
//
//			SpaceDocument spaceDoc = batchUnit.getSpaceDocument();
//			SpaceTypeDescriptorHolder spaceTypeDescriptor = types.get(batchUnit
//					.getTypeName());
//
//			SpaceDocumentMapper<DBObject> mapper = getMapper(spaceTypeDescriptor
//					.getTypeDescriptor());
//
//			// DBObject obj = mapper.maps(spaceDoc);
//			DBObject obj = mapper.toDBObject(spaceDoc);
//
//			DBCollection col = getCollection(batchUnit.getTypeName());
//
//			switch (batchUnit.getDataSyncOperationType()) {
//
//			case WRITE:
//			case UPDATE:
//
//				col.save(obj);
//				break;
//			case PARTIAL_UPDATE: // TODO: add partial update and change api
//									// support and wiki documentaion
//			case CHANGE:// TODO: add partial update and change api support and
//						// wiki documentaion
//				col.update(new BasicDBObject("_id", obj.get("_id")),
//						removeNulls(obj));
//				break;
//			// case REMOVE_BY_UID: // TODO: not supported by cassandra
//			// implementation
//			case REMOVE:
//				col.remove(obj);
//				break;
//			default:
//				throw new IllegalStateException(
//						"Unsupported data sync operation type: "
//								+ batchUnit.getDataSyncOperationType());
//
//			}
//		}
//		// }
//	}
//
//	private DBObject removeNulls(DBObject obj) {
//		BasicDBObject result = new BasicDBObject();
//
//		for (String key : obj.keySet()) {
//
//			if ("_id".equals(key))
//				continue;
//
//			Object value = obj.get(key);
//
//			if (value == null)
//				continue;
//
//			if (value instanceof DBObject)
//				result.append("$set", new BasicDBObject(key,
//						removeNulls((DBObject) value)));
//			else
//				result.append("$set", new BasicDBObject(key, value));
//
//		}
//
//		return result;
//	}
//
//	protected SpaceDocumentMapper<DBObject> getMapper(
//			SpaceTypeDescriptor spaceTypeDescriptor) {
//
//		SpaceDocumentMapper<DBObject> mapper = _mappingCache
//				.get(spaceTypeDescriptor.getTypeName());
//
//		if (mapper == null) {
//			// mapper = new DefaultPojoToMongoMapper(spaceTypeDescriptor);
//			mapper = new SpaceDocumentMapperImpl(spaceTypeDescriptor);
//			_mappingCache.put(spaceTypeDescriptor.getTypeName(), mapper);
//		}
//
//		return mapper;
//	}
//
//	public void cacheSpaceTypeDesciptor(SpaceTypeDescriptor spaceTypeDescriptor) {
//
//		if (spaceTypeDescriptor == null)
//			throw new IllegalArgumentException(
//					"spaceTypeDescriptor can not be null");
//
//		if (!types.containsKey(spaceTypeDescriptor.getTypeName())) {
//			introduceType(spaceTypeDescriptor);
//		}
//
//		SpaceTypeDescriptorHolder holder = new SpaceTypeDescriptorHolder(
//				spaceTypeDescriptor);
//
//		types.put(spaceTypeDescriptor.getTypeName(), holder);
//	}
//
//	public void close() {
//
//		client.close();
//	}
//
//	public Collection<SpaceTypeDescriptor> loadMetadata() {
//
//		DBCollection metadata = getConnection().getCollection(
//				METADATA_COLLECTION_NAME);
//
//		DBCursor cursor = metadata.find();
//
//		while (cursor.hasNext()) {
//
//			DBObject type = cursor.next();
//
//			Object b = type.get(TYPE_DESCRIPTOR_FIELD_NAME);
//
//			readMetadata(b);
//		}
//
//		return getSortedTypes();
//	}
//
//	/**
//	 * read object as byte array of type {@link SpaceTypeDescriptor} then ensure
//	 * indexes and put it in types cache
//	 * 
//	 * @param b
//	 *            - object to be casted to type array
//	 */
//	private void readMetadata(Object b) {
//		try {
//
//			ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
//					(byte[]) b));
//
//			Serializable typeDescriptorVersionedSerializableWrapper = IOUtils
//					.readObject(in);
//
//			SpaceTypeDescriptor spaceTypeDescriptor = SpaceTypeDescriptorVersionedSerializationUtils
//					.fromSerializableForm(typeDescriptorVersionedSerializableWrapper);
//
//			indexBuilder.ensureIndexes(spaceTypeDescriptor);
//
//			cacheSpaceTypeDesciptor(spaceTypeDescriptor);
//
//		} catch (ClassNotFoundException e) {
//			logger.error(e);
//			throw new SpaceMongoDataSourceException(
//					ERROR_OCCURS_WHILE_TRY_DESERIALIZE_OBJECT + b, e);
//		} catch (IOException e) {
//			logger.error(e);
//			throw new SpaceMongoDataSourceException(
//					ERROR_OCCURS_WHILE_TRY_DESERIALIZE_OBJECT + b, e);
//		}
//	}
//
//	/**
//	 * Encapsulate {@link DataSyncOperation} into batch helper POJO and create
//	 * new batch list
//	 * 
//	 * @param dataSyncOperations
//	 */
//	public void performBatch(DataSyncOperation[] dataSyncOperations) {
//		int length = dataSyncOperations.length;
//
//		List<BatchUnit> rows = new LinkedList<BatchUnit>();
//
//		for (int index = 0; index < length; index++) {
//
//			BatchUnit bu = new BatchUnit();
//			DataSyncOperation dso = dataSyncOperations[index];
//
//			cacheSpaceTypeDesciptor(dso.getTypeDescriptor());
//
//			bu.setSpaceDocument(dso.getDataAsDocument());
//			bu.setDataSyncOperationType(dso.getDataSyncOperationType());
//
//			rows.add(bu);
//		}
//
//		performBatch(rows);
//	}
//
//	/**
//	 * @return - returned sorted list regard of inheritance hierarchy supper
//	 *         class ascending
//	 */
//	public Collection<SpaceTypeDescriptor> getSortedTypes() {
//
//		return TypeHierarcyTopologySorter.getSortedList(types);
//	}
//
//	public void ensureIndexes(AddIndexData addIndexData) {
//		indexBuilder.ensureIndexes(addIndexData);
//	}
//}
