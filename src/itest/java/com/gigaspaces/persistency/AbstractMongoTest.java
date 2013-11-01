package com.gigaspaces.persistency;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.openspaces.itest.persistency.cassandra.mock.MockIntroduceTypeData;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.gigaspaces.metadata.index.SpaceIndexType;

public abstract class AbstractMongoTest {

	private static final String LOCALHOST = "127.0.0.1";

	protected final MongoTestServer server = new MongoTestServer();
	protected MongoSpaceSynchronizationEndpoint _syncInterceptor;
	protected MongoSpaceDataSource _dataSource;
	private MongoClientWrapper _syncInterceptorClient;
	private MongoClientWrapper _dataSourceClient;

	@Before
	public void initialSetup() {

		server.initialize(isEmbedded());

		_syncInterceptorClient = createMongoClientWrapper("cluster-sync");
		_syncInterceptor = createMongoSyncEndpointInterceptor(_syncInterceptorClient);
		_dataSourceClient = createMongoClientWrapper("cluster-datasource");
		_dataSource = createMongoSpaceDataSource(_dataSourceClient);
	}

	@After
	public void finalTeardown() {

		if (_syncInterceptorClient != null) {
			_syncInterceptorClient.close();
		}

		if (_dataSourceClient != null) {
			_dataSourceClient.close();
		}

		if (_dataSource != null) {
			_dataSource.close();
		}
		
		server.destroy();
	}

	protected boolean isEmbedded() {
		return false;
	}

	protected MongoSpaceDataSource createMongoSpaceDataSource(
			MongoClientWrapper _dataSourceClient2) {

		MongoSpaceDataSource dataSource = new MongoSpaceDataSourceConfigurer()
				.mongoClientPool(_dataSourceClient2).create();

		return dataSource;
	}

	protected MongoSpaceSynchronizationEndpoint createMongoSyncEndpointInterceptor(
			MongoClientWrapper client) {

		MongoSpaceSynchronizationEndpoint syncInterceptor = new MongoSpaceSynchronizationEndpointConfigurer()
				.mongoClientPool(client).create();

		return syncInterceptor;
	}

	protected MongoClientWrapper createMongoClientWrapper(String cluterName) {
		MongoClientWrapper client = new MongoClientWrapperConfigurer()
				.host(LOCALHOST).port(0).db(cluterName).create();

		return client;
	}

	protected MockIntroduceTypeData createIntroduceTypeDataFromSpaceDocument(
			SpaceDocument document, String key) {
		return createIntroduceTypeDataFromSpaceDocument(document, key,
				new HashSet<String>());
	}

	protected MockIntroduceTypeData createIntroduceTypeDataFromSpaceDocument(
			SpaceDocument document, String key, Set<String> indexes) {
		SpaceTypeDescriptorBuilder builder = new SpaceTypeDescriptorBuilder(
				document.getTypeName());
		for (Entry<String, Object> entry : document.getProperties().entrySet())
			builder.addFixedProperty(entry.getKey(), entry.getValue()
					.getClass());
		for (String index : indexes)
			builder.addPathIndex(index, SpaceIndexType.BASIC);
		builder.idProperty(key);
		return new MockIntroduceTypeData(builder.create());
	}

}
