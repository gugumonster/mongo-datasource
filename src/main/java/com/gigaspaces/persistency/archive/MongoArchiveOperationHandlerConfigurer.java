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

import org.openspaces.core.GigaSpace;

import com.mongodb.MongoClient;

//import com.allanbank.mongodb.MongoClientConfiguration;

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
	 * @see MongoArchiveOperationHandler#setConfig(MongoClientConfiguration)
	 */
	public MongoArchiveOperationHandlerConfigurer config(
			MongoClient config) {
		handler.setConfig(config);
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
