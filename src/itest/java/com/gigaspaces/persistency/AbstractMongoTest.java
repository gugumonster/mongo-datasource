package com.gigaspaces.persistency;

import java.util.HashSet;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.openspaces.itest.persistency.cassandra.mock.MockIntroduceTypeData;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.gigaspaces.metadata.index.SpaceIndexType;

public abstract class AbstractMongoTest {

	private static final int PORT = 12345;
	protected static final Random random = new Random();
	private static final String LOCALHOST = "127.0.0.1";

	protected final MongoTestServer server = new MongoTestServer();
	protected MongoSpaceSynchronizationEndpoint _syncInterceptor;
	protected MongoSpaceDataSource _dataSource;
	private MongoClientWrapper _syncInterceptorClient;
	private MongoClientWrapper _dataSourceClient;

	@Before
	public void initialSetup() {

		server.initialize();

		_syncInterceptorClient = createMongoClientWrapper(server.getDBName());
		_syncInterceptor = createMongoSyncEndpointInterceptor(_syncInterceptorClient);
		_dataSourceClient = createMongoClientWrapper(server.getDBName());
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

	protected MongoSpaceDataSource createMongoSpaceDataSource(
			MongoClientWrapper _dataSourceClient2) {

		MongoSpaceDataSource dataSource = new MongoSpaceDataSourceConfigurer()
				.mongoClientWrapper(_dataSourceClient2).create();

		return dataSource;
	}

	protected MongoSpaceSynchronizationEndpoint createMongoSyncEndpointInterceptor(
			MongoClientWrapper client) {

		MongoSpaceSynchronizationEndpoint syncInterceptor = new MongoSpaceSynchronizationEndpointConfigurer()
				.mongoClientWrapper(client).create();

		return syncInterceptor;
	}

	protected MongoClientWrapper createMongoClientWrapper(String dbName) {
		MongoClientWrapper client = new MongoClientWrapperConfigurer()
				.host(LOCALHOST).port(PORT).db(dbName).create();

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
