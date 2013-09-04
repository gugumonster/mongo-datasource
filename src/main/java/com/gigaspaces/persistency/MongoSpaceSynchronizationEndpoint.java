package com.gigaspaces.persistency;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.bson.BSON;

import com.gigaspaces.internal.client.utils.SerializationUtil;
import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.internal.io.MarshObject;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorVersionedSerializationUtils;
import com.gigaspaces.metadata.StorageType;
import com.gigaspaces.sync.AddIndexData;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.IntroduceTypeData;
import com.gigaspaces.sync.OperationsBatchData;
import com.gigaspaces.sync.SpaceSynchronizationEndpoint;
import com.gigaspaces.sync.TransactionData;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class MongoSpaceSynchronizationEndpoint extends
		SpaceSynchronizationEndpoint {

	private MongoClientPool mongoClientPool;

	public MongoSpaceSynchronizationEndpoint(MongoClientPool mongoClientPool) {
		this.mongoClientPool = mongoClientPool;
	}

	@Override
	public void onIntroduceType(IntroduceTypeData introduceTypeData) {

		SpaceTypeDescriptor t = introduceTypeData.getTypeDescriptor();

		DB db = mongoClientPool.checkOut();

		DBCollection m = db.getCollection("metadata");

		BasicDBObject obj = new BasicDBObject();

		obj.append("_id", t.getTypeSimpleName());

		try {

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			ObjectOutputStream out = new ObjectOutputStream(bos);

			IOUtils.writeObject(out,
					SpaceTypeDescriptorVersionedSerializationUtils
							.toSerializableForm(t));

			obj.append("value", bos.toByteArray());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		WriteResult wr = m.save(obj, WriteConcern.SAFE);

		System.err.println(wr);
	}

	@Override
	public void onAddIndex(AddIndexData addIndexData) {
		super.onAddIndex(addIndexData);
	}

	@Override
	public void onOperationsBatchSynchronization(OperationsBatchData batchData) {
		DataSyncOperation dataSyncOperations[] = batchData.getBatchDataItems();

		doSynchronization(dataSyncOperations);
	}

	@Override
	public void onTransactionSynchronization(TransactionData transactionData) {
		DataSyncOperation dataSyncOperations[] = transactionData
				.getTransactionParticipantDataItems();

		doSynchronization(dataSyncOperations);
	}

	public void close() {
		mongoClientPool.close();
	}

	private void doSynchronization(DataSyncOperation dataSyncOperations[]) {
		mongoClientPool.performBatch(dataSyncOperations);
	}
}
