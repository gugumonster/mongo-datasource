package com.gigaspaces.persistency;

public class MongoSpaceDataSourceConfigurer {

	private MongoClientPool mongoClientPool;

	public MongoSpaceDataSourceConfigurer mongoClientPool(
			MongoClientPool mongoClientPool) {
		this.mongoClientPool = mongoClientPool;
		return this;
	}
	
	public MongoSpaceDataSource create(){
		return new MongoSpaceDataSource(mongoClientPool);
	}
}
