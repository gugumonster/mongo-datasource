package com.gigaspaces.persistency;

public class MongoSpaceSynchronizationEndpointConfigurer {

	private MongoClientPool mongoClientPool;

	public MongoSpaceSynchronizationEndpointConfigurer mongoClientPool(
			MongoClientPool mongoClientPool) {
		this.mongoClientPool = mongoClientPool;
		return this;
	}
	
	public MongoSpaceSynchronizationEndpoint create(){
		return new MongoSpaceSynchronizationEndpoint(mongoClientPool);
	}
}
