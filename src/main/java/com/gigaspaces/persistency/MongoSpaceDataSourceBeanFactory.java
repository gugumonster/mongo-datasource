package com.gigaspaces.persistency;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class MongoSpaceDataSourceBeanFactory implements
		FactoryBean<MongoSpaceDataSource>, InitializingBean, DisposableBean {

	private final MongoSpaceDataSourceConfigurer configurer = getConfigurer();
	
	private MongoSpaceDataSource mongoSpaceDataSource;
	
	public void setMongoClientPool(MongoClientPool mongoClientPool){
		configurer.mongoClientPool(mongoClientPool);
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
