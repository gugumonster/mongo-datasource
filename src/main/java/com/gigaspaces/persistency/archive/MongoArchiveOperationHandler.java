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

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.archive.ArchiveOperationHandler;
import org.springframework.beans.factory.annotation.Required;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.persistency.MongoClientPool;
import com.gigaspaces.persistency.MongoClientPoolConfigurer;
import com.gigaspaces.persistency.error.SpaceMongoException;
import com.gigaspaces.persistency.metadata.BatchUnit;
import com.gigaspaces.sync.DataSyncOperationType;
import com.mongodb.ServerAddress;

/**
 * 
 * @author Shadi Massalha
 * 
 */
@SuppressWarnings("restriction")
public class MongoArchiveOperationHandler implements ArchiveOperationHandler {

	private final Log logger = LogFactory.getLog(this.getClass());

	private ServerAddress[] addresses;
	private String db;
	private String user;
	private String password;

	private MongoClientPool client;

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

		createMongoClient();
	}

	private void createMongoClient() {
		client = new MongoClientPoolConfigurer().addresses(addresses).db(db)
				.user(user).password(password).create();
	}

	/**
	 * @param db
	 * @see MongoClientPoolConfigurer#db(String)
	 */
	@Required
	public void setDb(String db) {
		this.db = db;

	}

	/**
	 * @param user
	 * @see MongoClientPoolConfigurer#user(String)
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @param password
	 * @see MongoClientPoolConfigurer#password(String)
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param addresses
	 * @see MongoClientPoolConfigurer#addresses(ServerAddress[])
	 */
	@Required
	public void setAddresses(ServerAddress[] addresses) {
		this.addresses = addresses;
	}
}
