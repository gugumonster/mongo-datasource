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
//import javax.net.SocketFactory;
//
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.InitializingBean;
//
//import com.mongodb.DBDecoderFactory;
//import com.mongodb.DBEncoderFactory;
//import com.mongodb.MongoClientOptions;
//import com.mongodb.MongoClientOptions.Builder;
//import com.mongodb.ReadPreference;
//import com.mongodb.WriteConcern;
//
///**
// * 
// * Bean Factory which provide setter methods for
// * {@link MongoClientOptions.Builder } which is the class builder of mongodb
// * driver options class {@link MongoClientOptions }
// * 
// * @see <a
// *      href="http://api.mongodb.org/java/current/com/mongodb/MongoClientOptions.Builder.html">MongoClientOptions.Builder</a>
// *      
// * @author Shadi Massalha
// * 
// */
//public class MongoClientOptionsBeanFactory implements
//		FactoryBean<MongoClientOptions>, InitializingBean, DisposableBean {
//
//	private MongoClientOptions options;
//
//	private final MongoClientOptions.Builder configurer = getConfigurer();
//
//	public void setAlwaysUseMBeans(boolean alwaysUseMBeans) {
//		configurer.alwaysUseMBeans(alwaysUseMBeans);
//	}
//
//	public void setAutoConnectRetry(boolean autoConnectRetry) {
//		configurer.autoConnectRetry(autoConnectRetry);
//	}
//
//	public void setConnectionsPerHost(int connectionsPerHost) {
//		configurer.connectionsPerHost(connectionsPerHost);
//	}
//
//	public void setConnectTimeout(int connectTimeout) {
//		configurer.connectTimeout(connectTimeout);
//	}
//
//	public void setCursorFinalizerEnabled(boolean cursorFinalizerEnabled) {
//		configurer.cursorFinalizerEnabled(cursorFinalizerEnabled);
//	}
//
//	public void setDbDecoderFactory(DBDecoderFactory dbDecoderFactory) {
//		configurer.dbDecoderFactory(dbDecoderFactory);
//	}
//
//	public void setDbEncoderFactory(DBEncoderFactory dbEncoderFactory) {
//		configurer.dbEncoderFactory(dbEncoderFactory);
//	}
//
//	public void setDescription(String description) {
//		configurer.description(description);
//	}
//
//	public void setMaxAutoConnectRetryTime(long maxAutoConnectRetryTime) {
//		configurer.maxAutoConnectRetryTime(maxAutoConnectRetryTime);
//	}
//
//	public void setMaxWaitTime(int maxWaitTime) {
//		configurer.maxWaitTime(maxWaitTime);
//	}
//
//	public void setReadPreference(ReadPreference readPreference) {
//		configurer.readPreference(readPreference);
//	}
//
//	public void setSocketFactory(SocketFactory socketFactory) {
//		configurer.socketFactory(socketFactory);
//	}
//
//	public void setSocketKeepAlive(boolean socketKeepAlive) {
//		configurer.socketKeepAlive(socketKeepAlive);
//	}
//
//	public void setSocketTimeout(int socketTimeout) {
//		configurer.socketTimeout(socketTimeout);
//	}
//
//	public void setThreadsAllowedToBlockForConnectionMultiplier(
//			int threadsAllowedToBlockForConnectionMultiplier) {
//		configurer
//				.threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier);
//	}
//
//	public void setWriteConcern(WriteConcern writeConcern) {
//		configurer.writeConcern(writeConcern);
//	}
//
//	public void destroy() throws Exception {
//
//	}
//
//	private Builder getConfigurer() {
//		return new Builder();
//	}
//
//	public void afterPropertiesSet() throws Exception {
//		this.options = configurer.build();
//
//	}
//
//	public MongoClientOptions getObject() throws Exception {
//
//		return options;
//	}
//
//	public Class<?> getObjectType() {
//		return MongoClientOptions.class;
//	}
//
//	public boolean isSingleton() {
//		return false;
//	}
//
//}
