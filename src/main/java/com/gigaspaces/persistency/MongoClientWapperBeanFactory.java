///*******************************************************************************
// * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *******************************************************************************/
//package com.gigaspaces.persistency;
//
//import java.util.List;
//
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.InitializingBean;
//
//import com.mongodb.MongoClientOptions;
//import com.mongodb.MongoClientURI;
//import com.mongodb.MongoCredential;
//import com.mongodb.ServerAddress;
//
///**
// * @author Shadi Massalha
// * 
// *         default spring bean factory implementation that can get external
// *         {@link MongoClientWrapperConfigurer} as configurar otherwise create
// *         it own one
// */
//public class MongoClientWapperBeanFactory implements
//		FactoryBean<MongoClientWrapper>, InitializingBean, DisposableBean,
//		MongoClientConfiguration {
//
//	private MongoClientWrapper mongoClientWrapper;
//
//	private final MongoClientWrapperConfigurer configurer = getConfigurer();
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.gigaspaces.persistency.MongoClientConfiguration#setSeeds(java.util
//	 * .List)
//	 */
//	public void setSeeds(List<ServerAddress> seeds) {
//		configurer.seeds(seeds);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.gigaspaces.persistency.MongoClientConfiguration#setCredentials(java
//	 * .util.List)
//	 */
//	public void setCredentials(List<MongoCredential> credentials) {
//		configurer.credentials(credentials);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.gigaspaces.persistency.MongoClientConfiguration#setOptions(com.mongodb
//	 * .MongoClientOptions)
//	 */
//	public void setOptions(MongoClientOptions options) {
//		configurer.options(options);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.gigaspaces.persistency.MongoClientConfiguration#setAddr(com.mongodb
//	 * .ServerAddress)
//	 */
//	public void setAddr(ServerAddress addr) {
//		configurer.addr(addr);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.gigaspaces.persistency.MongoClientConfiguration#setHost(java.lang
//	 * .String)
//	 */
//	public void setHost(String host) {
//		configurer.host(host);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.gigaspaces.persistency.MongoClientConfiguration#setPort(int)
//	 */
//	public void setPort(int port) {
//		configurer.port(port);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.gigaspaces.persistency.MongoClientConfiguration#setUri(com.mongodb
//	 * .MongoClientURI)
//	 */
//	public void setUri(MongoClientURI uri) {
//		configurer.uri(uri);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.gigaspaces.persistency.MongoClientConfiguration#setDb(java.lang.String
//	 * )
//	 */
//	public void setDb(String db) {
//		configurer.db(db);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.gigaspaces.persistency.MongoClientConfiguration#setUser(java.lang
//	 * .String)
//	 */
//	public void setUser(String user) {
//		configurer.user(user);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.gigaspaces.persistency.MongoClientConfiguration#setPassword(java.
//	 * lang.String)
//	 */
//	public void setPassword(String password) {
//		configurer.password(password);
//	}
//
//	public void destroy() throws Exception {
//		mongoClientWrapper.close();
//	}
//
//	private MongoClientWrapperConfigurer getConfigurer() {
//
//		return new MongoClientWrapperConfigurer();
//	}
//
//	public void afterPropertiesSet() throws Exception {
//		this.mongoClientWrapper = configurer.create();
//	}
//
//	public MongoClientWrapper getObject() throws Exception {
//		return mongoClientWrapper;
//	}
//
//	public Class<?> getObjectType() {
//		return MongoClientWrapper.class;
//	}
//
//	public boolean isSingleton() {
//		return true;
//	}
//}
