package com.gigaspaces.persistency.metadata;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.sync.DataSyncOperationType;

/**
 * helper class that hold metadata for batch operation
 * 
 * @author Shadi Massalha
 * 
 */
public class BatchUnit {

	private SpaceDocument spaceDocument;
	private String typeName;
	private DataSyncOperationType dataSyncOperationType;

	public SpaceDocument getSpaceDocument() {
		return spaceDocument;
	}

	public String getTypeName() {
		return typeName;
	}

	public DataSyncOperationType getDataSyncOperationType() {
		return dataSyncOperationType;
	}

	public void setSpaceDocument(SpaceDocument spaceDocument) {
		if (spaceDocument == null)
			throw new IllegalArgumentException("spaceDocument can not be null");

		this.spaceDocument = spaceDocument;
		this.typeName = spaceDocument.getTypeName();
	}

	public void setDataSyncOperationType(
			DataSyncOperationType dataSyncOperationType) {
		this.dataSyncOperationType = dataSyncOperationType;
	}

}
