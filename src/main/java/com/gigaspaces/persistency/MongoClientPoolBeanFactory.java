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

import com.mongodb.ServerAddress;

public class MongoClientPoolBeanFactory implements
		FactoryBean<MongoClientPool>, InitializingBean, DisposableBean {

	private MongoClientPool mongoClientPool;

	private final MongoClientPoolConfigurer configurer = getConfigurer();

	public void setAddresses(ServerAddress[] addresses) {
		configurer.addresses(addresses);		
	}

	public void setDb(String db) {
		configurer.db(db);
	}

	public void setUser(String user) {
		configurer.user(user);
	}

	public void setPassword(String password) {
		configurer.password(password);
	}

	public void destroy() throws Exception {
		mongoClientPool.close();
	}

	private MongoClientPoolConfigurer getConfigurer() {
		return new MongoClientPoolConfigurer();
	}

	public void afterPropertiesSet() throws Exception {
		this.mongoClientPool = configurer.create();
	}

	public MongoClientPool getObject() throws Exception {
		return mongoClientPool;
	}

	public Class<?> getObjectType() {
		return MongoClientPool.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
