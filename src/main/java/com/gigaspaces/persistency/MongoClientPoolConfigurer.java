package com.gigaspaces.persistency;

import org.springframework.util.StringUtils;

import com.mongodb.ServerAddress;

/**
 * @author Shadi Massalha
 *
 */
public class MongoClientPoolConfigurer {

	private ServerAddress[] addresses;
	private String db;
	private String user;
	private String password;

	public MongoClientPoolConfigurer addresses(ServerAddress[] addresses) {
		this.addresses = addresses;
		return this;
	}

	public MongoClientPoolConfigurer db(String db){
		this.db=db;
		return this;
	}
	public MongoClientPoolConfigurer user(String user) {
		this.user = user;
		return this;
	}

	public MongoClientPoolConfigurer password(String password) {
		this.password = password;
		return this;
	}

	public MongoClientPool create() {

		if (addresses == null)
			throw new IllegalArgumentException("addresses must be set");

		if (addresses.length == 0)
			throw new IllegalArgumentException(
					"addresses must have minimum one address definition");

		if(!StringUtils.hasLength(db))
			throw new IllegalArgumentException("db can not be null or empty");
		
		return new MongoClientPool(addresses[0],db);
	}
}
