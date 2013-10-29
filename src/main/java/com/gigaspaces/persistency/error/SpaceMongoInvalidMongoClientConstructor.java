package com.gigaspaces.persistency.error;

public class SpaceMongoInvalidMongoClientConstructor extends
		SpaceMongoException {
	public SpaceMongoInvalidMongoClientConstructor(String message, Throwable e) {
		super(message, e);
	}

	public SpaceMongoInvalidMongoClientConstructor(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
