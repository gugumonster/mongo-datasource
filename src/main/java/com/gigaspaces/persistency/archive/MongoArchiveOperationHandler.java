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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.archive.ArchiveOperationHandler;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Required;

import com.allanbank.mongodb.MongoClientConfiguration;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.persistency.MongoClientConnector;
import com.gigaspaces.persistency.MongoClientConnectorConfigurer;
import com.gigaspaces.persistency.error.SpaceMongoException;
import com.gigaspaces.persistency.metadata.BatchUnit;
import com.gigaspaces.sync.DataSyncOperationType;

/**
 * @author Shadi Massalha
 */
@SuppressWarnings("restriction")
public class MongoArchiveOperationHandler implements ArchiveOperationHandler {

	private final Log logger = LogFactory.getLog(this.getClass());

	// injected(required)
	private GigaSpace gigaSpace;

	private MongoClientConnector client;

	private MongoClientConfiguration config;
	private String db;

	@Required
	public void setGigaSpace(GigaSpace gigaSpace) {
		this.gigaSpace = gigaSpace;
	}

	/**
	 * @see ArchiveOperationHandler#archive(Object...)
	 * 
	 * @throws SpaceMongoException
	 *             - Problem encountered while archiving to mongodb
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

			BatchUnit batchUnit = new BatchUnit();

			batchUnit.setSpaceDocument((SpaceDocument) object);
			((SpaceDocument) object).getTypeName();
			batchUnit.setDataSyncOperationType(DataSyncOperationType.WRITE);

			rows.add(batchUnit);
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Writing to mongo " + rows.size() + " objects");
		}
		// TODO: check if type descriptor is empty gigaspace ref
		client.performBatch(rows);
	}

	/**
	 * @see ArchiveOperationHandler#supportsBatchArchiving()
	 * @return true - Since Multiple archiving of the exact same objects is
	 *         supported (idempotent).
	 */
	public boolean supportsBatchArchiving() {
		return true;
	}

	@PostConstruct
	public void afterPropertiesSet() {

		if (gigaSpace == null) {
			throw new IllegalArgumentException("gigaSpace cannot be null");
		}

		createMongoClient();
	}

	private void createMongoClient() {

		client = new MongoClientConnectorConfigurer().config(config).db(db)
				.create();
	}

	public GigaSpace getGigaSpace() {
		return gigaSpace;
	}

	/**
	 * @see MongoClientConnectorConfigurer#db(String)
	 */
	@Required
	public void setDb(String db) {
		this.db = db;

	}

	/**
	 * @see MongoClientConnectorConfigurer#config(MongoClientConfiguration)
	 */
	@Required
	public void setConfig(MongoClientConfiguration config) {
		this.config = config;
	}

	@PreDestroy
	public void destroy() {
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				throw new SpaceMongoArchiveOperationHandlerException(
						"can not close mongo client", e);
			}
		}
	}

	public MongoClientConfiguration getConfig() {
		return config;
	}
}
