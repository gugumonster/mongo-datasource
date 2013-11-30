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

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.allanbank.mongodb.MongoClientConfiguration;

/**
 * @author Shadi Massalha
 * 
 *         default spring bean factory implementation that can get external
 *         {@link MongoClientConnectorConfigurer} as configurar otherwise create
 *         it own one
 */
public class MongoClientConnectorBeanFactory implements
		FactoryBean<MongoClientConnector>, InitializingBean, DisposableBean {

	private MongoClientConnector mongoClientConnector;

	private final MongoClientConnectorConfigurer configurer = getConfigurer();

	/**
	 * @param db
	 *            - the name of the target mongo db
	 */
	public void setDb(String db) {
		configurer.db(db);
	}

	/**
	 * @param config
	 *            - encapsulate all configuration option of mongo db driver
	 *            server address, port, write concern, credintial ...
	 * @see <a href=
	 *      "http://www.allanbank.com/mongodb-async-driver/apidocs/com/allanbank/mongodb/MongoClientConfiguration.html"
	 *      >com.allanbank.mongodb.MongoClientConfiguration</a>
	 */
	public void setConfig(MongoClientConfiguration config) {
		configurer.config(config);
	}

	public void destroy() throws Exception {
		mongoClientConnector.close();
	}

	private MongoClientConnectorConfigurer getConfigurer() {

		return new MongoClientConnectorConfigurer();
	}

	public void afterPropertiesSet() throws Exception {
		this.mongoClientConnector = configurer.create();
	}

	public MongoClientConnector getObject() throws Exception {
		return mongoClientConnector;
	}

	public Class<?> getObjectType() {
		return MongoClientConnector.class;
	}

	public boolean isSingleton() {
		return true;
	}
}
