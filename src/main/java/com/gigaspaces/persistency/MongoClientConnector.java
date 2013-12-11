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
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
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
 * MongoDB driver client wrapper
 *
 * @author Shadi Massalha
 */
public class MongoClientConnector {

	private static final String TYPE_DESCRIPTOR_FIELD_NAME = "value";
	private static final String METADATA_COLLECTION_NAME = "metadata";

	private static final Log logger = LogFactory.getLog(MongoClientConnector.class);

	private final MongoClient client;
	private final String dbName;
    private final IndexBuilder indexBuilder;

	// TODO: shadi must add documentation
	private static final Map<String, SpaceTypeDescriptorHolder> types = new ConcurrentHashMap<String, SpaceTypeDescriptorHolder>();
	private static final Map<String, SpaceDocumentMapper<Document>> mappingCache = new ConcurrentHashMap<String, SpaceDocumentMapper<Document>>();

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

        MongoCollection m = getConnection().getCollection(METADATA_COLLECTION_NAME);

		DocumentBuilder builder = BuilderFactory.start()
		    .add(Constants.ID_PROPERTY, typeDescriptor.getTypeName());

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			IOUtils.writeObject(out, SpaceTypeDescriptorVersionedSerializationUtils.toSerializableForm(typeDescriptor));

			builder.add(TYPE_DESCRIPTOR_FIELD_NAME, bos.toByteArray());

			int wr = m.save(builder);

			if (logger.isTraceEnabled())
				logger.trace(wr);

			indexBuilder.ensureIndexes(typeDescriptor);

		} catch (IOException e) {
			logger.error(e);

			throw new SpaceMongoException("error occurs while serialize and save type descriptor: " + typeDescriptor, e);
		}
	}

	public MongoDatabase getConnection() {
        return client.getDatabase(dbName);
	}

	public MongoCollection getCollection(String collectionName) {

		return getConnection().getCollection(collectionName);
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

		List<Future<? extends Number>> pending = new ArrayList<Future<? extends Number>>();

        for (BatchUnit row : rows) {
            SpaceDocument spaceDoc = row.getSpaceDocument();
            SpaceTypeDescriptorHolder typeDescriptorHolder = types.get(row.getTypeName());

            SpaceDocumentMapper<Document> mapper = getMapper(typeDescriptorHolder.getTypeDescriptor());

            DocumentAssignable obj = mapper.toDBObject(spaceDoc);

            MongoCollection col = getCollection(row.getTypeName());

            switch (row.getDataSyncOperationType()) {

                case WRITE:
                case UPDATE:
                    pending.add(col.saveAsync(obj));
                    break;
                case PARTIAL_UPDATE:
                case CHANGE:
                    Document query = BuilderFactory.start()
                            .add(Constants.ID_PROPERTY, ((Document) obj).get(Constants.ID_PROPERTY).getValueAsObject())
                            .build();

                    Document update = normalize((Document) obj);
                    pending.add(col.updateAsync(query, update));
                    break;
                // case REMOVE_BY_UID: // Not supported by this implementation
                case REMOVE:
                    pending.add(col.deleteAsync(obj, false));
                    break;
                default:
                    throw new IllegalStateException("Unsupported data sync operation type: "
                            + row.getDataSyncOperationType());
            }
        }

		long totalCount = waitFor(pending);

		if (logger.isTraceEnabled()) {
			logger.trace("total accepted replies is: " + totalCount);
		}
	}

    public Collection<SpaceTypeDescriptor> loadMetadata() {

		MongoCollection metadata = getCollection(METADATA_COLLECTION_NAME);

		MongoIterator<Document> cursor = metadata.find(BuilderFactory.start().build());

		while (cursor.hasNext()) {
			Document type = cursor.next();
			Object b = type.get(TYPE_DESCRIPTOR_FIELD_NAME).getValueAsObject();
			readMetadata(b);
		}

		return getSortedTypes();
	}

    public Collection<SpaceTypeDescriptor> getSortedTypes() {

        return TypeHierarcyTopologySorter.getSortedList(types);
    }

    private void cacheTypeDescriptor(SpaceTypeDescriptor typeDescriptor) {

        if (typeDescriptor == null)
            throw new IllegalArgumentException("typeDescriptor can not be null");

        if (!types.containsKey(typeDescriptor.getTypeName()))
            introduceType(typeDescriptor);

        types.put(typeDescriptor.getTypeName(), new SpaceTypeDescriptorHolder(typeDescriptor));
    }

	private void readMetadata(Object b) {
		try {

			ObjectInput in = new CustomClassLoaderObjectInputStream(MongoClientConnector.class.getClassLoader(),
			                                                        new ByteArrayInputStream((byte[]) b));
			Serializable typeDescWrapper = IOUtils.readObject(in);
			SpaceTypeDescriptor typeDescriptor = SpaceTypeDescriptorVersionedSerializationUtils.fromSerializableForm(
                    typeDescWrapper);
			indexBuilder.ensureIndexes(typeDescriptor);

			cacheTypeDescriptor(typeDescriptor);

		} catch (ClassNotFoundException e) {
			logger.error(e);
			throw new SpaceMongoDataSourceException("Failed to deserialize: " + b, e);
		} catch (IOException e) {
			logger.error(e);
            throw new SpaceMongoDataSourceException("Failed to deserialize: " + b, e);
		}
	}

	public void ensureIndexes(AddIndexData addIndexData) {
		indexBuilder.ensureIndexes(addIndexData);
	}

    private static SpaceDocumentMapper<Document> getMapper(SpaceTypeDescriptor typeDescriptor) {

        SpaceDocumentMapper<Document> mapper = mappingCache.get(typeDescriptor.getTypeName());
        if (mapper == null) {
            mapper = new AsyncSpaceDocumentMapper(typeDescriptor);
            mappingCache.put(typeDescriptor.getTypeName(), mapper);
        }

        return mapper;
    }

    private static Document normalize(Document obj) {

        DocumentBuilder builder = BuilderFactory.start();

        for (Element e : obj.getElements()) {

            String key = e.getName();

            if (Constants.ID_PROPERTY.equals(key))
                continue;

            Object value = obj.get(key).getValueAsObject();

            if (value == null)
                continue;
//
//			if (value instanceof DocumentAssignable) {
//				builder.push("$set").add(key, (Document) value);
//			} else
            builder.push("$set").add(key, value);
        }

        return builder.build();
    }

    private static long waitFor(List<Future<? extends Number>> replies) {

        long total = 0;

        for (Future<? extends Number> future : replies) {
            try {
                total += future.get().longValue();
            } catch (InterruptedException e) {
                throw new SpaceMongoException("Number of async operations: " + replies.size(), e);
            } catch (ExecutionException e) {
                throw new SpaceMongoException("Number of async operations: " + replies.size(), e);
            }
        }

        return total;
    }
    
    /**
     * Object input stream that uses a custom class loader to resolve classes 
     */
    static class CustomClassLoaderObjectInputStream extends ObjectInputStream {
      
      private final ClassLoader classLoader;
      
      CustomClassLoaderObjectInputStream(ClassLoader classLoader, InputStream is) throws IOException { 
        super(is);
        this.classLoader = classLoader;
      }
      
      @Override
      protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
        return Class.forName(desc.getName(), false, classLoader);
      }
      
    }
    
}
