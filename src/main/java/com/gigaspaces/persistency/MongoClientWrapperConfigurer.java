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

import java.net.UnknownHostException;
import java.util.List;

import org.springframework.util.StringUtils;

import com.gigaspaces.persistency.error.SpaceMongoInvalidMongoClientConstructor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * @author Shadi Massalha
 * 
 */
public class MongoClientWrapperConfigurer {

	private static final String LOCALHOST = "localhost";

	// mongodb name
	private String db;

	// Alternative or combined MongoClient constructor parameters;
	private List<ServerAddress> seeds; // 1
	private List<MongoCredential> credentials;// 2
	private MongoClientOptions options;// 4
	private String host = LOCALHOST;// 8
	private int port = 27017;// 16
	private ServerAddress addr;// 32
	private MongoClientURI uri;// 64
	private String user;
	private String password;

	public MongoClientWrapperConfigurer seeds(List<ServerAddress> seeds) {
		this.seeds = seeds;
		return this;
	}

	public MongoClientWrapperConfigurer credentials(
			List<MongoCredential> credentials) {
		this.credentials = credentials;
		return this;
	}

	public MongoClientWrapperConfigurer host(String host) {
		this.host = host;
		return this;
	}

	public MongoClientWrapperConfigurer port(int port) {
		this.port = port;
		return this;
	}

	public MongoClientWrapperConfigurer addr(ServerAddress addr) {
		this.addr = addr;
		return this;
	}

	public MongoClientWrapperConfigurer uri(MongoClientURI uri) {
		this.uri = uri;
		new MongoClientURI("");
		return this;
	}

	public MongoClientWrapperConfigurer options(MongoClientOptions options) {
		this.options = options;
		return this;
	}

	public MongoClientWrapperConfigurer db(String db) {
		this.db = db;
		return this;
	}

	public MongoClientWrapperConfigurer user(String user) {
		this.user = user;
		return this;
	}

	public MongoClientWrapperConfigurer password(String password) {
		this.password = password;
		return this;
	}

	public MongoClientWrapper create() {

		if (!StringUtils.hasLength(db))
			throw new IllegalArgumentException("db can not be null or empty");

		if (!StringUtils.hasLength(host))
			throw new IllegalArgumentException("host can not be null or empty");

		if (port <= 1024)
			throw new IllegalArgumentException("port must be gratter than 1024");

		MongoClient client;

		try {
			client = createMongoClient();
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}

		if (StringUtils.hasLength(user) || StringUtils.hasLength(password))
			return new MongoClientWrapper(client, db, user, password);

		return new MongoClientWrapper(client, db);
	}

	private int getConstructorId() {
		int constructorId = 0;

		if (seeds != null && seeds.size() > 0)
			constructorId += 1;

		if (credentials != null && credentials.size() > 0)
			constructorId += 2;

		if (options != null)
			constructorId += 4;

		if (StringUtils.hasLength(host) && !LOCALHOST.equals(host))
			constructorId += 8;

		if (port > 1024 && port != 27017)
			constructorId += 16;

		if (addr != null)
			constructorId += 32;

		if (uri != null)
			constructorId += 64;

		return constructorId;
	}

	private MongoClient createMongoClient() throws UnknownHostException {

		int constructorId = getConstructorId();

		switch (constructorId) {
		case 0:
			return new MongoClient();
		case 1:
			return new MongoClient(seeds);
		case 3:
			return new MongoClient(seeds, credentials);
		case 5:
			return new MongoClient(seeds, options);
		case 7:
			return new MongoClient(seeds, credentials, options);
		case 8:
			return new MongoClient(host);
		case 12:
			return new MongoClient(host, options);
		case 24:
			return new MongoClient(host, port);
		case 32:
			return new MongoClient(addr);
		case 34:
			return new MongoClient(addr, credentials);
		case 36:
			return new MongoClient(addr, options);
		case 38:
			return new MongoClient(addr, credentials, options);
		case 64:
			return new MongoClient(uri);
		}

		throw new SpaceMongoInvalidMongoClientConstructor(
				"invalid constructor id " + constructorId);
	}
}
