package com.gigaspaces.persistency;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class MongoSpaceSynchronizationEndpointBeanFactory implements
		FactoryBean<MongoSpaceSynchronizationEndpoint>, InitializingBean,
		DisposableBean {

	private final MongoSpaceSynchronizationEndpointConfigurer configurer = getConfigurer();
	private MongoSpaceSynchronizationEndpoint mongoSpaceSynchronizationEndpoint;

	public void setMongoClientPool(MongoClientPool mongoClientPool){
		configurer.mongoClientPool(mongoClientPool);
	}
	
	public void destroy() throws Exception {
		mongoSpaceSynchronizationEndpoint.close();
	}

	private MongoSpaceSynchronizationEndpointConfigurer getConfigurer() {
		return new MongoSpaceSynchronizationEndpointConfigurer();
	}

	public void afterPropertiesSet() throws Exception {
		this.mongoSpaceSynchronizationEndpoint = configurer.create();

	}

	public MongoSpaceSynchronizationEndpoint getObject() throws Exception {
		return mongoSpaceSynchronizationEndpoint;
	}

	public Class<?> getObjectType() {
		return MongoSpaceSynchronizationEndpoint.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
