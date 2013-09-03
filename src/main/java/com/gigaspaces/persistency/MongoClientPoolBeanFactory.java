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
