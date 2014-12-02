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

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorVersionedSerializationUtils;
import com.gigaspaces.persistency.error.SpaceMongoDataSourceException;
import com.gigaspaces.persistency.error.SpaceMongoException;
import com.gigaspaces.persistency.metadata.BatchUnit;
import com.gigaspaces.persistency.metadata.DefaultSpaceDocumentMapper;
import com.gigaspaces.persistency.metadata.IndexBuilder;
import com.gigaspaces.persistency.metadata.SpaceDocumentMapper;
import com.gigaspaces.sync.AddIndexData;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.IntroduceTypeData;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.persistency.support.SpaceTypeDescriptorContainer;
import org.openspaces.persistency.support.TypeDescriptorUtils;

/**
 * MongoDB driver client wrapper
 * 
 * @author Shadi Massalha
 */
public class MongoClientConnector{

	private static final String DOLLAR_SIGN = "__d_s__";
	private static final String TYPE_DESCRIPTOR_FIELD_NAME = "value";
	private static final String METADATA_COLLECTION_NAME = "metadata";

	private static final Log logger = LogFactory
			.getLog(MongoClientConnector.class);

	private final MongoClient client;
	private final String dbName;
	private final IndexBuilder indexBuilder;

	// TODO: shadi must add documentation
	private static final Map<String, SpaceTypeDescriptorContainer> types = new ConcurrentHashMap<String, SpaceTypeDescriptorContainer>();
	private static final Map<String, SpaceDocumentMapper<DBObject>> mappingCache = new ConcurrentHashMap<String, SpaceDocumentMapper<DBObject>>();

    public MongoClientConnector(MongoClient client, String db) {

		this.client = client;
		this.dbName = db;
		this.indexBuilder = new IndexBuilder(this);
	}

    public void close() throws IOException {

		client.close();
	}

	public void introduceType(IntroduceTypeData introduceTypeData) {

		introduceType(introduceTypeData.getTypeDescriptor());
	}

	public void introduceType(SpaceTypeDescriptor typeDescriptor) {

        //CSI128-TPE: only introduce type in DB when annotated with @SpaceClass

        //Class<?> clazz = typeDescriptor.getClass();
        //SpaceClass spaceClassAnnotation = clazz.getAnnotation(SpaceClass.class);
        //if (spaceClassAnnotation != null) {

            DBCollection m = getConnection()
                    .getCollection(METADATA_COLLECTION_NAME);

            BasicDBObjectBuilder builder = BasicDBObjectBuilder.start().add(
                    Constants.ID_PROPERTY, typeDescriptor.getTypeName());
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bos);
                IOUtils.writeObject(
                        out,
                        SpaceTypeDescriptorVersionedSerializationUtils
                                .toSerializableForm(typeDescriptor));

                builder.add(TYPE_DESCRIPTOR_FIELD_NAME, bos.toByteArray());

                WriteResult wr = m.save(builder.get());

                if (logger.isTraceEnabled())
                    logger.trace(wr);

                indexBuilder.ensureIndexes(typeDescriptor);
            } catch (IOException e) {
                logger.error(e);

                throw new SpaceMongoException(
                        "error occurs while serialize and save type descriptor: "
                                + typeDescriptor, e);
            }
        //}
	}

	public DB getConnection() {
        return client.getDB(dbName);
	}

	public DBCollection getCollection(String collectionName) {

		return getConnection().getCollection(
                collectionName.replace("$", DOLLAR_SIGN));
	}

	public void performBatch(DataSyncOperation[] operations) {
		List<BatchUnit> rows = new LinkedList<BatchUnit>();

		for (DataSyncOperation operation : operations) {

			BatchUnit bu = new BatchUnit();
			cacheTypeDescriptor(operation.getTypeDescriptor());
			bu.setSpaceDocument(operation.getDataAsDocument());
			bu.setDataSyncOperationType(operation.getDataSyncOperationType());
			rows.add(bu);
		}

		performBatch(rows);
	}

	public void performBatch(List<BatchUnit> rows) {
		if (logger.isTraceEnabled()) {
			logger.trace("MongoClientWrapper.performBatch(" + rows + ")");
			logger.trace("Batch size to be performed is " + rows.size());
		}
		//List<Future<? extends Number>> pending = new ArrayList<Future<? extends Number>>();

		for (BatchUnit row : rows) {
			SpaceDocument spaceDoc = row.getSpaceDocument();
			SpaceTypeDescriptor typeDescriptor = types.get(row.getTypeName())
					.getTypeDescriptor();
			SpaceDocumentMapper<DBObject> mapper = getMapper(typeDescriptor);

			DBObject obj = mapper.toDBObject(spaceDoc);

			DBCollection col = getCollection(row.getTypeName());
			switch (row.getDataSyncOperationType()) {

			case WRITE:
			case UPDATE:
				col.save(obj);
				break;
			case PARTIAL_UPDATE:
				DBObject query = BasicDBObjectBuilder
						.start()
						.add(Constants.ID_PROPERTY,
								obj.get(Constants.ID_PROPERTY)).get();
				
				DBObject update = normalize(obj);
                col.update(query, update);
				break;
			// case REMOVE_BY_UID: // Not supported by this implementation
			case REMOVE:
				col.remove(obj);
				break;
			default:
				throw new IllegalStateException(
						"Unsupported data sync operation type: "
								+ row.getDataSyncOperationType());
			}
		}

		/*long totalCount = waitFor(pending);

		if (logger.isTraceEnabled()) {
			logger.trace("total accepted replies is: " + totalCount);
		}*/
	}

	public Collection<SpaceTypeDescriptor> loadMetadata(final Set<String> managedEntriesPrefixes) {

		DBCollection metadata = getCollection(METADATA_COLLECTION_NAME);

        // CSI128-TPE: load only those meta data objects where their id is prefixed with one of the managed ones
        DBObject query;
        if (managedEntriesPrefixes == null || managedEntriesPrefixes.isEmpty()) {
            logger.info("Loading metadata from DB - no managed entries specified --> searching for every entry");
            query = new BasicDBObject();
        } else {
            logger.info("Loading metadata from DB - managed entries specified --> using query");
            query = createManagedEntriesQuery(managedEntriesPrefixes);
        }

        DBCursor cursor = metadata.find(query);
        logger.info("Number of entries found in DB: " + cursor.size());

        while (cursor.hasNext()) {
			DBObject type = cursor.next();

			Object b = type.get(TYPE_DESCRIPTOR_FIELD_NAME);

            readMetadata(b);
		}

		return getSortedTypes();
	}

    private DBObject createManagedEntriesQuery(final Set<String> managedEntriesPrefixes) {
        BasicDBList regexList = new BasicDBList();
        for (String managedEntryPrefix : managedEntriesPrefixes) {
            Pattern regex = Pattern.compile("^" + managedEntryPrefix + ".*");
            DBObject clause = new BasicDBObject(Constants.ID_PROPERTY, regex);
            regexList.add(clause);
        }

        return new BasicDBObject("$or", regexList);
    }

    public Collection<SpaceTypeDescriptor> getSortedTypes() {

		return TypeDescriptorUtils.sort(types);
	}

	private void cacheTypeDescriptor(SpaceTypeDescriptor typeDescriptor) {

		if (typeDescriptor == null)
			throw new IllegalArgumentException("typeDescriptor can not be null");

		if (!types.containsKey(typeDescriptor.getTypeName()))
			introduceType(typeDescriptor);

		types.put(typeDescriptor.getTypeName(),
				new SpaceTypeDescriptorContainer(typeDescriptor));
	}

	private void readMetadata(Object b) {
		try {
			ObjectInput in = new ClassLoaderAwareInputStream(new ByteArrayInputStream((byte[]) b));
            Serializable typeDescWrapper = IOUtils.readObject(in); // we here get the exception
            SpaceTypeDescriptor typeDescriptor = SpaceTypeDescriptorVersionedSerializationUtils
					.fromSerializableForm(typeDescWrapper);

            logger.info("Loading metadata for: " + typeDescriptor.getTypeName());

            indexBuilder.ensureIndexes(typeDescriptor);

            cacheTypeDescriptor(typeDescriptor);

		} catch (ClassNotFoundException e) {
            logger.warn("Loading metadata from MongoDB - failed to deserialize as class could not be found: " + e.getMessage());
            //CSI128-TPE: we continue with the reading of meta data anyway.
            //SMELLS: This should not be done: Use Splitter

			//logger.error(e);
			//throw new SpaceMongoDataSourceException("Failed to deserialize: "
			//		+ b, e);
		} catch (IOException e) {
			logger.error(e);
			throw new SpaceMongoDataSourceException("Failed to deserialize: "
					+ b, e);
		}
	}

	public void ensureIndexes(AddIndexData addIndexData) {
		indexBuilder.ensureIndexes(addIndexData);
	}

	private static SpaceDocumentMapper<DBObject> getMapper(
			SpaceTypeDescriptor typeDescriptor) {

		SpaceDocumentMapper<DBObject> mapper = mappingCache.get(typeDescriptor
				.getTypeName());
		if (mapper == null) {
			mapper = new DefaultSpaceDocumentMapper(typeDescriptor);
			mappingCache.put(typeDescriptor.getTypeName(), mapper);
		}

		return mapper;
	}

	private static DBObject normalize(DBObject obj) {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();

		Iterator<String> iterator = obj.keySet().iterator();
        builder.push("$set");
		while (iterator.hasNext()) {

			String key = iterator.next();

			if (Constants.ID_PROPERTY.equals(key))
				continue;

			Object value = obj.get(key);

			if (value == null)
				continue;
			
			builder.add(key, value);
		}

		return builder.get();
	}

	private static long waitFor(List<Future<? extends Number>> replies) {

		long total = 0;

		for (Future<? extends Number> future : replies) {
			try {
				total += future.get().longValue();
			} catch (InterruptedException e) {
				throw new SpaceMongoException("Number of async operations: "
						+ replies.size(), e);
			} catch (ExecutionException e) {
				throw new SpaceMongoException("Number of async operations: "
						+ replies.size(), e);
			}
		}

		return total;
	}

    private static class ClassLoaderAwareInputStream extends ObjectInputStream{

        private ClassLoaderAwareInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        public Class resolveClass(ObjectStreamClass desc) throws IOException,
                ClassNotFoundException {
            ClassLoader currentTccl = null;
            try {
                currentTccl = Thread.currentThread().getContextClassLoader();
                return currentTccl.loadClass(desc.getName());
            } catch (Exception e) {
            }
            return super.resolveClass(desc);
        }
    }


}
