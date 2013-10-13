package com.gigaspaces.persistency.archive;

public class MongoArchiveOperationHandlerConfigurer {

	MongoArchiveOperationHandler handler;
	private boolean initialized;
	
	public MongoArchiveOperationHandlerConfigurer() {
		handler = new MongoArchiveOperationHandler();		
	}
	
	
	
}
