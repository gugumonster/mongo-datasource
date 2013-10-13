package com.gigaspaces.persistency.error;

public class MongoMetadataException extends SpaceMongoException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MongoMetadataException(String name) {
		super(String.format("invalid collection name", name));
	}

	

}
