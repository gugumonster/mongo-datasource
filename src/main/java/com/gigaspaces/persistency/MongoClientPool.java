package com.gigaspaces.persistency;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * @author Shadi Massalha
 * 
 */
public class MongoClientPool {

	private static final Log logger = LogFactory.getLog(MongoClientPool.class);

	private MongoClient client;
	private String dbName;

	public MongoClientPool(ServerAddress host, String db) {
		this.client = new MongoClient(host);
		this.dbName = db;
	}
	
	public synchronized DB checkOut() {
		DB db = client.getDB(dbName);

		return db;

	}

	public void close() {
		// TODO Auto-generated method stub
		
	}
}
