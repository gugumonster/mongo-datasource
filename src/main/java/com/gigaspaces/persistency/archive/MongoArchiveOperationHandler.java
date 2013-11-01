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
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.archive.ArchiveOperationHandler;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Required;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.persistency.MongoClientConfiguration;
import com.gigaspaces.persistency.MongoClientWrapper;
import com.gigaspaces.persistency.MongoClientWrapperConfigurer;
import com.gigaspaces.persistency.error.SpaceMongoException;
import com.gigaspaces.persistency.metadata.BatchUnit;
import com.gigaspaces.sync.DataSyncOperationType;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * 
 * @author Shadi Massalha
 * 
 */
@SuppressWarnings("restriction")
public class MongoArchiveOperationHandler implements ArchiveOperationHandler,
		MongoClientConfiguration {

	private final Log logger = LogFactory.getLog(this.getClass());

	// injected(required)
	private GigaSpace gigaSpace;

	private MongoClientWrapper client;

	private List<ServerAddress> seeds;
	private String db;
	private List<MongoCredential> credentials;
	private MongoClientOptions options;
	private ServerAddress addr;
	private String host;
	private int port;
	private MongoClientURI uri;
	private String password;
	private String user;

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
		client = new MongoClientWrapperConfigurer().seeds(seeds)
				.credentials(credentials).options(options).addr(addr).uri(uri)
				.host(host).port(port).user(user).password(password).db(db)
				.create();
	}

	public GigaSpace getGigaSpace() {
		return gigaSpace;
	}

	/**
	 * @see MongoClientWrapperConfigurer#db(String)
	 */
	@Required
	public void setDb(String db) {
		this.db = db;

	}

	/**
	 * @see MongoClientWrapperConfigurer#user(String)
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @see MongoClientWrapperConfigurer#password(String)
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @see MongoClientWrapperConfigurer#seeds(ServerAddress[])
	 */
	public void setSeeds(List<ServerAddress> seeds) {
		this.seeds = seeds;
	}

	/**
	 * @see com.gigaspaces.persistency.MongoClientConfiguration#setCredentials(java
	 *      .util.List)
	 */
	public void setCredentials(List<MongoCredential> credentials) {
		this.credentials = credentials;
	}

	/**
	 * @see com.gigaspaces.persistency.MongoClientConfiguration#setOptions(com.mongodb
	 *      .MongoClientOptions)
	 */
	public void setOptions(MongoClientOptions options) {
		this.options = options;
	}

	/**
	 * @see com.gigaspaces.persistency.MongoClientConfiguration#setAddr(com.mongodb
	 *      .ServerAddress)
	 */
	public void setAddr(ServerAddress addr) {
		this.addr = addr;
	}

	/**
	 * @see com.gigaspaces.persistency.MongoClientConfiguration#setHost(java.lang
	 *      .String)
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @see com.gigaspaces.persistency.MongoClientConfiguration#setPort(int)
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @see com.gigaspaces.persistency.MongoClientConfiguration#setUri(com.mongodb
	 *      .MongoClientURI)
	 */
	public void setUri(MongoClientURI uri) {
		this.uri = uri;
	}

	@PreDestroy
	public void destroy() {
		if (client != null) {
			client.close();
		}
	}
}
