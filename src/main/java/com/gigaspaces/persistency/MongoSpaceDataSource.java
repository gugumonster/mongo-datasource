package com.gigaspaces.persistency;

import java.net.UnknownHostException;
import java.util.Set;

import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.datasource.SpaceDataSource;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoSpaceDataSource extends SpaceDataSource {
	@Override
	public DataIterator<SpaceTypeDescriptor> initialMetadataLoad() {
		try {
			MongoClient mongoClient = new MongoClient( "localhost" );
			
			DB db = mongoClient.getDB("mydb");
			
			Set<String> collections = db.getCollectionNames();
			
			for(String col : collections){
				
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return super.initialMetadataLoad();
	}
}
