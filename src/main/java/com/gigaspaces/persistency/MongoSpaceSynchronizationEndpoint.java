package com.gigaspaces.persistency;

import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.sync.AddIndexData;
import com.gigaspaces.sync.IntroduceTypeData;
import com.gigaspaces.sync.OperationsBatchData;
import com.gigaspaces.sync.SpaceSynchronizationEndpoint;
import com.gigaspaces.sync.TransactionData;

public class MongoSpaceSynchronizationEndpoint extends
		SpaceSynchronizationEndpoint {

	private MongoClientPool mongoClientPool;

	public MongoSpaceSynchronizationEndpoint(MongoClientPool mongoClientPool) {
		this.mongoClientPool = mongoClientPool;
	}

	@Override
	public void onIntroduceType(IntroduceTypeData introduceTypeData) {
		
		SpaceTypeDescriptor t = introduceTypeData.getTypeDescriptor();
		
		String id = t.getIdPropertyName();
		
		
		super.onIntroduceType(introduceTypeData);
	}
	
	@Override
	public void onAddIndex(AddIndexData addIndexData) {	
		super.onAddIndex(addIndexData);
	}
	
	@Override
	public void onOperationsBatchSynchronization(OperationsBatchData batchData) {
		// TODO Auto-generated method stub
		super.onOperationsBatchSynchronization(batchData);
	}
	
	@Override
	public void onTransactionSynchronization(TransactionData transactionData) {
		// TODO Auto-generated method stub
		super.onTransactionSynchronization(transactionData);
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}
}
