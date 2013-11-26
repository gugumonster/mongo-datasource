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

public class MongoSpaceDataSourceBeanFactory implements
		FactoryBean<MongoSpaceDataSource>, InitializingBean, DisposableBean {

	private final MongoSpaceDataSourceConfigurer configurer = getConfigurer();

	private MongoSpaceDataSource mongoSpaceDataSource;

	public void setMongoClientConnector(MongoClientConnector mongoClientConnector) {
		configurer.mongoClientConnector(mongoClientConnector);
	}

	public void destroy() throws Exception {
		mongoSpaceDataSource.close();
	}

	private MongoSpaceDataSourceConfigurer getConfigurer() {
		return new MongoSpaceDataSourceConfigurer();
	}

	public void afterPropertiesSet() throws Exception {
		this.mongoSpaceDataSource = configurer.create();
	}

	public MongoSpaceDataSource getObject() throws Exception {
		return mongoSpaceDataSource;
	}

	public Class<?> getObjectType() {
		return MongoSpaceDataSource.class;
	}

	public boolean isSingleton() {
		return true;
	}
}
