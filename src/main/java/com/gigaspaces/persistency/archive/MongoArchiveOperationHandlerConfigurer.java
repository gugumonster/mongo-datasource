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

import java.util.List;

import org.openspaces.core.GigaSpace;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * @author Shadi Massalha
 * 
 */
/**
 * @author Shadi Massalha
 * 
 */
public class MongoArchiveOperationHandlerConfigurer {

	MongoArchiveOperationHandler handler;
	private boolean initialized;

	public MongoArchiveOperationHandlerConfigurer() {
		handler = new MongoArchiveOperationHandler();
	}

	/**
	 * @see MongoArchiveOperationHandler#setDb(String)
	 */
	public MongoArchiveOperationHandlerConfigurer db(String db) {
		handler.setDb(db);
		return this;
	}

	/**
	 * @see MongoArchiveOperationHandler#setUser(String)
	 */
	public MongoArchiveOperationHandlerConfigurer user(String user) {
		handler.setUser(user);
		return this;
	}

	/**
	 * @see MongoArchiveOperationHandler#setPassword(String)
	 */
	public MongoArchiveOperationHandlerConfigurer password(String password) {
		handler.setPassword(password);
		return this;
	}

	/**
	 * @see MongoArchiveOperationHandler#setSeeds(List)
	 */
	public MongoArchiveOperationHandlerConfigurer seeds(
			List<ServerAddress> seeds) {
		handler.setSeeds(seeds);
		return this;
	}

	/**
	 * @see MongoArchiveOperationHandler#setCredentials(List)
	 */
	public MongoArchiveOperationHandlerConfigurer credentials(
			List<MongoCredential> credentials) {
		handler.setCredentials(credentials);
		return this;
	}

	/**
	 * @see MongoArchiveOperationHandler#setOptions(MongoClientOptions)
	 */
	public MongoArchiveOperationHandlerConfigurer options(
			MongoClientOptions options) {
		handler.setOptions(options);
		return this;
	}

	/**
	 * @see MongoArchiveOperationHandler#setHost(String)
	 */
	public MongoArchiveOperationHandlerConfigurer host(String host) {
		handler.setHost(host);
		return this;
	}

	/**
	 * @see MongoArchiveOperationHandler#setPort(int)
	 */
	public MongoArchiveOperationHandlerConfigurer port(int port) {
		handler.setPort(port);
		return this;
	}

	/**
	 * @see MongoArchiveOperationHandler#setAddr(ServerAddress)
	 */
	public MongoArchiveOperationHandlerConfigurer addr(ServerAddress addr) {
		handler.setAddr(addr);
		return this;
	}

	/**
	 * @see MongoArchiveOperationHandler#setUri(MongoClientURI)
	 */
	public MongoArchiveOperationHandlerConfigurer uri(MongoClientURI uri) {
		handler.setUri(uri);
		return this;
	}

	/**
	 * @see MongoArchiveOperationHandler#setGigaSpace(GigaSpace)
	 */
	public MongoArchiveOperationHandlerConfigurer gigaSpace(GigaSpace gigaSpace) {
		handler.setGigaSpace(gigaSpace);
		return this;
	}

	public MongoArchiveOperationHandler create() {

		if (!initialized) {

			handler.afterPropertiesSet();

			initialized = true;
		}

		return handler;
	}

}
