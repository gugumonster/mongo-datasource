//package com.gigaspaces.persistency;
//
//import java.util.List;
//
//import com.mongodb.MongoClientOptions;
//import com.mongodb.MongoClientURI;
//import com.mongodb.MongoCredential;
//import com.mongodb.ServerAddress;
//
///**
// * 
// * configuration interface that define all mongodb client configuration options
// * combinations.
// * 
// * TODO: documentation with link for mongo driver API
// * 
// * @author Shadi Massalha
// * 
// * 
// */
//public interface MongoClientConfiguration {
//
//	public void setSeeds(List<ServerAddress> seeds);
//
//	public void setCredentials(List<MongoCredential> credentials);
//
//	public void setOptions(MongoClientOptions options);
//
//	public void setAddr(ServerAddress addr);
//
//	public void setHost(String host);
//
//	public void setPort(int port);
//
//	public void setUri(MongoClientURI uri);
//
//	public void setDb(String db);
//
//	public void setUser(String user);
//
//	public void setPassword(String password);
//
//}