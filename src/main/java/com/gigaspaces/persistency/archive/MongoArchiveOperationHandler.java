/*******************************************************************************
 * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.gigaspaces.persistency.archive;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.archive.ArchiveOperationHandler;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.persistency.error.SpaceMongoException;
import com.gigaspaces.persistency.metadata.BatchUnit;
import com.gigaspaces.sync.DataSyncOperationType;

/**
 * 
 * @author Shadi Massalha
 * 
 */
public class MongoArchiveOperationHandler implements ArchiveOperationHandler {

	private final Log logger = LogFactory.getLog(this.getClass());

    /**
     * @see ArchiveOperationHandler#archive(Object...)
     * 
     *  @throws SpaceMongoException - Problem encountered while archiving to mongodb
     */
	public void archive(Object... objects) {

		List<BatchUnit> rows = new LinkedList<BatchUnit>();

		for (Object object : objects) {

			if (!(object instanceof SpaceDocument)) {
				throw new SpaceMongoArchiveOperationHandlerException(
						object.getClass()
								+ " is not supported since it is not a "
								+ SpaceDocument.class.getName());
			}

			BatchUnit batchUnit=new BatchUnit();
			
			batchUnit.setSpaceDocument((SpaceDocument) object);
			batchUnit.setDataSyncOperationType(DataSyncOperationType.WRITE);
									
			rows.add(batchUnit);			
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Writing to mongo " + rows.size() + " objects");
		}
	}

	/**
	 * @see ArchiveOperationHandler#supportsBatchArchiving()
	 * @return true - Since Multiple archiving of the exact same objects is
	 *         supported (idempotent).
	 */
	public boolean supportsBatchArchiving() {
		return true;
	}
}
