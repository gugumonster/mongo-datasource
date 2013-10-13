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
	private SpaceTypeDescriptor spaceTypeDescriptor;
	private DataSyncOperationType dataSyncOperationType;
	
	public SpaceDocument getSpaceDocument() {
		return spaceDocument;
	}
	
	public SpaceTypeDescriptor getSpaceTypeDescriptor() {
		return spaceTypeDescriptor;
	}
	
	public DataSyncOperationType getDataSyncOperationType() {
		return dataSyncOperationType;
	}
	
	public void setSpaceDocument(SpaceDocument spaceDocument) {
		this.spaceDocument = spaceDocument;
	}
	
	public void setSpaceTypeDescriptor(SpaceTypeDescriptor spaceTypeDescriptor) {
		this.spaceTypeDescriptor = spaceTypeDescriptor;
	}
	
	public void setDataSyncOperationType(DataSyncOperationType dataSyncOperationType) {
		this.dataSyncOperationType = dataSyncOperationType;
	}

	
}
