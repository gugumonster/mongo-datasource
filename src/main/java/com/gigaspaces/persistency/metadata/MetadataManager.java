package com.gigaspaces.persistency.metadata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorVersionedSerializationUtils;
import com.gigaspaces.persistency.MongoClientPool;
import com.gigaspaces.persistency.MongoSpaceSynchronizationEndpoint;
import com.gigaspaces.sync.AddIndexData;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.IntroduceTypeData;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class MetadataManager {

	private static final String TYPE_DESCRIPTOR_FIELD_NAME = "value";
	private static final String DEFAULT_ID = "_id";
	private static final String METADATA_COLLECTION_NAME = "metadata";

	private static final Log logger = LogFactory
			.getLog(MongoSpaceSynchronizationEndpoint.class);

	private final MongoClientPool pool;
	private IndexBuilder indexBuilder;

	public MetadataManager(MongoClientPool pool) {
		if (pool == null)
			throw new IllegalArgumentException("mongo client can not be null");

		this.pool = pool;
		this.indexBuilder = new IndexBuilder(pool);
	}

	public void introduceType(IntroduceTypeData introduceTypeData) {

		SpaceTypeDescriptor t = introduceTypeData.getTypeDescriptor();

		DBCollection m = getPool().getCollection(METADATA_COLLECTION_NAME);

		BasicDBObject obj = new BasicDBObject();

		obj.append(DEFAULT_ID, t.getTypeName());

		writeMetadata(introduceTypeData, t, m, obj);
	}

	/**
	 * serialize the type descriptor to binary stream and save it to metadata
	 * collection
	 * 
	 * @param introduceTypeData
	 * @param t
	 * @param m
	 * @param obj
	 */
	private void writeMetadata(IntroduceTypeData introduceTypeData,
			SpaceTypeDescriptor t, DBCollection m, BasicDBObject obj) {
		try {

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			ObjectOutputStream out = new ObjectOutputStream(bos);

			IOUtils.writeObject(out,
					SpaceTypeDescriptorVersionedSerializationUtils
							.toSerializableForm(t));

			obj.append(TYPE_DESCRIPTOR_FIELD_NAME, bos.toByteArray());

			WriteResult wr = m.save(obj, WriteConcern.SAFE);

			logger.trace(wr);

			indexBuilder.ensureIndexes(introduceTypeData.getTypeDescriptor());

		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void ensureIndexes(AddIndexData addIndexData) {
		indexBuilder.ensureIndexes(addIndexData);
	}

	public MongoClientPool getPool() {
		return pool;
	}

	public void performBatch(DataSyncOperation[] dataSyncOperations) {
		pool.performBatch(dataSyncOperations);

	}

	public void close() {
		pool.close();

	}

	public LinkedList<SpaceTypeDescriptor> loadMetadata()
			throws ClassNotFoundException, IOException {

		DBCollection metadata = pool.getCollection(METADATA_COLLECTION_NAME);

		DBCursor m = metadata.find();

		LinkedList<SpaceTypeDescriptor> types = new LinkedList<SpaceTypeDescriptor>();

		while (m.hasNext()) {
			DBObject type = m.next();

			Object b = type.get(TYPE_DESCRIPTOR_FIELD_NAME);

			readMetadata(types, b);
		}

		return types;
	}

	private void readMetadata(LinkedList<SpaceTypeDescriptor> types, Object b)
			throws ClassNotFoundException, IOException {
		try {

			ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
					(byte[]) b));

			Serializable typeDescriptorVersionedSerializableWrapper = IOUtils
					.readObject(in);

			SpaceTypeDescriptor spaceTypeDescriptor = SpaceTypeDescriptorVersionedSerializationUtils
					.fromSerializableForm(typeDescriptorVersionedSerializableWrapper);

			types.add(spaceTypeDescriptor);

			indexBuilder.ensureIndexes(spaceTypeDescriptor);

		} catch (ClassNotFoundException e) {
			logger.error(e);

			throw e;

		} catch (IOException e) {
			logger.error(e);
			throw e;
		}
	}
}
