package com.gigaspaces.persistency.error;

public class MongoMetadataException extends Exception {

	public MongoMetadataException(String name) {
		super(String.format("invalid collection name", name));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8121486259267830343L;

}
