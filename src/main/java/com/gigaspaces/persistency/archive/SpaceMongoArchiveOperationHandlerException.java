package com.gigaspaces.persistency.archive;

import com.gigaspaces.persistency.error.SpaceMongoException;

public class SpaceMongoArchiveOperationHandlerException extends SpaceMongoException {

	private static final long serialVersionUID = 1L;

	public SpaceMongoArchiveOperationHandlerException(String message) {
		super(message);
	}

	public SpaceMongoArchiveOperationHandlerException(String message,
			Throwable e) {
		super(message, e);
	}
}
