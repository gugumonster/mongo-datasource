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
package com.gigaspaces.persistency;

import org.springframework.util.StringUtils;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.MongoFactory;

/**
 * @author Shadi Massalha
 * 
 */
public class MongoClientConnectorConfigurer {

	private String db;

	private MongoClientConfiguration config;

	/**
	 * @param config
	 *            - encapsulate all configuration option of mongo db driver
	 *            server address, port, write concern, credintial ...
	 * @see <a href=
	 *      "http://www.allanbank.com/mongodb-async-driver/apidocs/com/allanbank/mongodb/MongoClientConfiguration.html"
	 *      >com.allanbank.mongodb.MongoClientConfiguration</a>
	 */
	public MongoClientConnectorConfigurer config(MongoClientConfiguration config) {
		this.config = config;

		return this;
	}

	/**
	 * @param db
	 *            - the name of the target mongo db
	 */
	public MongoClientConnectorConfigurer db(String db) {
		this.db = db;
		return this;
	}

	public MongoClientConnector create() {

		if (!StringUtils.hasLength(db))
			throw new IllegalArgumentException("db can not be null or empty");

		if (config == null)
			throw new IllegalArgumentException("port must be gratter than 1024");

		MongoClient client = MongoFactory.createClient(config);

		return new MongoClientConnector(client, db);
	}
}
