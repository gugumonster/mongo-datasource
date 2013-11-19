package com.gigaspaces.persistency.itest;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.openspaces.itest.persistency.cassandra.mock.MockIntroduceTypeData;

import com.allanbank.mongodb.MongoClientConfiguration;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.gigaspaces.metadata.index.SpaceIndexType;
import com.gigaspaces.persistency.MongoClientWrapperConfigurer;
import com.gigaspaces.persistency.MongoClientWrapper;
import com.gigaspaces.persistency.MongoSpaceDataSource;
import com.gigaspaces.persistency.MongoSpaceDataSourceConfigurer;
import com.gigaspaces.persistency.MongoSpaceSynchronizationEndpoint;
import com.gigaspaces.persistency.MongoSpaceSynchronizationEndpointConfigurer;
import com.mongodb.ServerAddress;

public abstract class AbstractMongoTest {

	private static final int PORT = 27017;
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

		_syncInterceptorClient = createMongoClientWrapperV1rapper(server
				.getDBName());
		_syncInterceptor = createMongoSyncEndpointInterceptor(_syncInterceptorClient);
		_dataSourceClient = createMongoClientWrapperV1rapper(server.getDBName());
		_dataSource = createMongoSpaceDataSource(_dataSourceClient);
	}

	@After
	public void finalTeardown() throws IOException {

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

	protected MongoClientWrapper createMongoClientWrapperV1rapper(
			String dbName) {

		ServerAddress addr = null;
		try {
			addr = new ServerAddress(LOCALHOST, PORT);
		} catch (UnknownHostException e) {
			throw new AssertionError(e);
		}

		MongoClientConfiguration config = new MongoClientConfiguration();

		config.addServer(addr.getSocketAddress());				
		
		MongoClientWrapper client = new MongoClientWrapperConfigurer()
				.config(config).db(dbName).create();		

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
