package com.gigaspaces.persistency;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gigaspaces.persistency.metadata.MetadataManager;
import com.gigaspaces.sync.AddIndexData;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.IntroduceTypeData;
import com.gigaspaces.sync.OperationsBatchData;
import com.gigaspaces.sync.SpaceSynchronizationEndpoint;
import com.gigaspaces.sync.TransactionData;

/**
 * @author Shadi Massalha
 * 
 * 
 *         mongo db {@link SpaceSynchronizationEndpoint } implementation
 */
public class MongoSpaceSynchronizationEndpoint extends
		SpaceSynchronizationEndpoint {

	private static final Log logger = LogFactory
			.getLog(MongoSpaceSynchronizationEndpoint.class);

	private final MetadataManager metadataManager;

	public MongoSpaceSynchronizationEndpoint(MongoClientPool pool) {

		this.metadataManager = new MetadataManager(pool);
	}

	@Override
	public void onIntroduceType(IntroduceTypeData introduceTypeData) {

		if (logger.isDebugEnabled())
			logger.trace("MongoSpaceSynchronizationEndpoint.onIntroduceType("
					+ introduceTypeData + ")");

		metadataManager.introduceType(introduceTypeData);
	}

	@Override
	public void onAddIndex(AddIndexData addIndexData) {
		if (logger.isDebugEnabled())
			logger.debug("MongoSpaceSynchronizationEndpoint.onAddIndex("
					+ addIndexData + ")");

		metadataManager.ensureIndexes(addIndexData);
	}

	@Override
	public void onOperationsBatchSynchronization(OperationsBatchData batchData) {

		if (logger.isDebugEnabled())
			logger.debug("MongoSpaceSynchronizationEndpoint.onOperationsBatchSynchronization()");

		DataSyncOperation dataSyncOperations[] = batchData.getBatchDataItems();

		doSynchronization(dataSyncOperations);
	}

	@Override
	public void onTransactionSynchronization(TransactionData transactionData) {

		if (logger.isDebugEnabled())
			logger.debug("MongoSpaceSynchronizationEndpoint.onTransactionSynchronization()");

		DataSyncOperation dataSyncOperations[] = transactionData
				.getTransactionParticipantDataItems();

		doSynchronization(dataSyncOperations);
	}

	public void close() {
		if (logger.isDebugEnabled())
			logger.trace("MongoSpaceSynchronizationEndpoint.close()");
		 metadataManager.close();
	}

	private void doSynchronization(DataSyncOperation dataSyncOperations[]) {
		if (logger.isDebugEnabled())
			logger.trace("MongoSpaceSynchronizationEndpoint.doSynchronization()");

		metadataManager.performBatch(dataSyncOperations);
	}
}
