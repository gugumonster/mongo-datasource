package com.gigaspaces.persistency;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorVersionedSerializationUtils;
import com.gigaspaces.persistency.metadata.IndexBuilder;
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

/**
 * @author Shadi Massalha
 * 
 */
public class MongoSpaceSynchronizationEndpoint extends
		SpaceSynchronizationEndpoint {

//	private static final Log logger = LogFactory
//			.getLog(MongoSpaceSynchronizationEndpoint.class);

	private MongoClientPool pool;
	private IndexBuilder indexBuilder;

	public MongoSpaceSynchronizationEndpoint(MongoClientPool pool) {
		this.pool = pool;
		this.indexBuilder = new IndexBuilder(pool);
	}

	@Override
	public void onIntroduceType(IntroduceTypeData introduceTypeData) {

		//logger.trace("onIntroduceType(" + introduceTypeData + ")");

		SpaceTypeDescriptor t = introduceTypeData.getTypeDescriptor();

		DB db = pool.checkOut();

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

			WriteResult wr = m.save(obj, WriteConcern.SAFE);

			//logger.trace(wr);
			
			
			indexBuilder.ensureIndexes(introduceTypeData.getTypeDescriptor());
			
		} catch (IOException e) {
			//logger.error(e);
		}

	}

	@Override
	public void onAddIndex(AddIndexData addIndexData) {

		indexBuilder.ensureIndexes(addIndexData);
		// super.onAddIndex(addIndexData);
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

		//logger.trace("MongoSpaceSynchronizationEndpoint.close()");

		pool.close();
	}

	private void doSynchronization(DataSyncOperation dataSyncOperations[]) {

		//logger.trace("MongoSpaceSynchronizationEndpoint.doSynchronization()");

		pool.performBatch(dataSyncOperations);
	}
}
