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

import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;


/**
 * @author Shadi Massalha
 */
public class MongoClientConnectorConfigurer {

	private String db;
	private MongoClient client;
	
	public MongoClientConnectorConfigurer client(MongoClient client) {
		this.client = client;
		return this;
	}

	public MongoClientConnectorConfigurer db(String db) {
		this.db = db;
		return this;
	}

	public MongoClientConnector create() {

		if (!StringUtils.hasLength(db))
			throw new IllegalArgumentException("Argument cannot be null or empty: db");

		if (client == null)
            throw new IllegalArgumentException("Argument cannot be null or empty: config");
		
		return new MongoClientConnector(client, db);
	}
}
