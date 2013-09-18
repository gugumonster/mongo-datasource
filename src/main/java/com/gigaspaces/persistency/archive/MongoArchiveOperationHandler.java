package com.gigaspaces.persistency.archive;

import org.openspaces.archive.ArchiveOperationHandler;

public class MongoArchiveOperationHandler implements ArchiveOperationHandler {

	public void archive(Object... objects) {
	}

	public boolean supportsBatchArchiving() {		
		return false;
	}
}
