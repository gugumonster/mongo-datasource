package com.gigaspaces.persistency.itest.archive;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.allanbank.mongodb.Durability;
import com.allanbank.mongodb.MongoClientConfiguration;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.gigaspaces.metadata.index.SpaceIndexType;
import com.gigaspaces.persistency.archive.MongoArchiveOperationHandler;
import com.gigaspaces.persistency.archive.MongoArchiveOperationHandlerConfigurer;
import com.gigaspaces.persistency.error.SpaceMongoException;
import com.gigaspaces.persistency.itest.MongoTestServer;
import com.j_spaces.core.IJSpace;

public class TestMongoArchiveOperationHandler {

	private static final String SPACE_DOCUMENT_NAME_KEY = "Name";

	private static final String SPACEDOCUMENT_NAME = "Anvil";

	private static final String SPACEDOCUMENT_TYPENAME = "Product";

	private static final String SPACEDOCUMENT_ID = "hw-1234";

	private final String TEST_NAMESPACE_XML = "/itest/archive/test-mongo-archive-handler-namespace.xml";
	private final String TEST_RAW_XML = "/itest/archive/test-mongo-archive-handler-raw.xml";

	private final MongoTestServer server = new MongoTestServer();

	private boolean skipRegisterTypeDescriptor;

	@Before
	public void startServer() {
		server.initialize();
	}

	@After
	public void stopServer() {
		server.destroy();
	}

	/**
	 * Tests archiver with namespace spring bean xml
	 */
	@Test
	public void testXmlRaw() {
		xmlTest(TEST_RAW_XML);
	}

	/**
	 * Tests archiver with namespace spring bean xml
	 */
	@Test
	public void testXmlNamespace() {
		xmlTest(TEST_NAMESPACE_XML);
	}

	@Test
	public void testConfigurer() throws Exception {
		configurerTest();
	}

	@Test(expected = SpaceMongoException.class)
	public void testNoTypeDescriptorInSpace() throws Exception {
		skipRegisterTypeDescriptor = true;
		configurerTest();
	}

	private void configurerTest() throws Exception {
		final UrlSpaceConfigurer urlSpaceConfigurer = new UrlSpaceConfigurer(
				"/./space");

		MongoArchiveOperationHandler archiveHandler = null;

		try {
			GigaSpace gigaSpace;

			final IJSpace space = urlSpaceConfigurer.create();
			gigaSpace = new GigaSpaceConfigurer(space).create();

			MongoClientConfiguration config = new MongoClientConfiguration(
					"mongodb://" + server.getHost() + ":" + server.getPort()
							+ "/" + server.getDBName());

			archiveHandler = new MongoArchiveOperationHandlerConfigurer()
					.db(server.getDBName()).config(config).gigaSpace(gigaSpace)
					.create();

			test(archiveHandler, gigaSpace);
		} finally {

			if (urlSpaceConfigurer != null) {
				urlSpaceConfigurer.destroy();
			}

			if (archiveHandler != null) {
				archiveHandler.destroy();
			}
		}
	}

	private void xmlTest(String relativeXmlName) {

		final boolean refreshNow = false;
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { relativeXmlName }, refreshNow);

		PropertyPlaceholderConfigurer propertyConfigurer = new PropertyPlaceholderConfigurer();
		Properties properties = new Properties();
		properties.put("mongodb.db", server.getDBName());
		properties.put("mongodb.host", server.getHost());
		properties.put("mongodb.port", "" + server.getPort());
		properties.put("mongodb.durability", "ACK");
		propertyConfigurer.setProperties(properties);
		context.addBeanFactoryPostProcessor(propertyConfigurer);
		context.refresh();

		try {
			final MongoArchiveOperationHandler archiveHandler = context
					.getBean(MongoArchiveOperationHandler.class);

			Assert.assertEquals(Durability.ACK, archiveHandler.getConfig()
					.getDefaultDurability());
			final GigaSpace gigaSpace = context
					.getBean(org.openspaces.core.GigaSpace.class);
			test(archiveHandler, gigaSpace);
		} finally {
			context.close();
		}
	}

	private void test(MongoArchiveOperationHandler archiveHandler,
			GigaSpace gigaSpace) {

		if (!skipRegisterTypeDescriptor) {
			registerTypeDescriptor(gigaSpace);
		}

		archiveHandler.archive(createSpaceDocument());

		verifyDocumentInCassandra();
	}

	private SpaceDocument createSpaceDocument() {
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("CatalogNumber", SPACEDOCUMENT_ID);
		properties.put("Category", "Hardware");
		properties.put(SPACE_DOCUMENT_NAME_KEY, SPACEDOCUMENT_NAME);
		properties.put("Price", 9.99f);
		final SpaceDocument document = new SpaceDocument(
				SPACEDOCUMENT_TYPENAME, properties);
		return document;
	}

	private void registerTypeDescriptor(GigaSpace gigaSpace) {
		final SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(
				SPACEDOCUMENT_TYPENAME)
				.idProperty("CatalogNumber")
				.routingProperty("Category")
				.addPropertyIndex(SPACE_DOCUMENT_NAME_KEY, SpaceIndexType.BASIC)
				.addPropertyIndex("Price", SpaceIndexType.EXTENDED).create();
		gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);
	}

	private void verifyDocumentInCassandra() {
		// TODO: check this logic for mongo
		//
		// Cluster cluster =
		// HFactory.getOrCreateCluster("test-localhost_"+server.getPort(),
		// server.getHost()+ ":" + server.getPort());
		// Keyspace keyspace = HFactory.createKeyspace(server.getKeySpaceName(),
		// cluster);
		//
		// String columnFamilyName = SPACEDOCUMENT_TYPENAME; // as long as
		// shorter
		// // than 40 bytes
		// ThriftColumnFamilyTemplate<String, String> template = new
		// ThriftColumnFamilyTemplate<String, String>(
		// keyspace, columnFamilyName, StringSerializer.get(),
		// StringSerializer.get());
		//
		// Assert.assertTrue(SPACEDOCUMENT_TYPENAME + " does not exist",
		// template.isColumnsExist(SPACEDOCUMENT_ID));
		//
		// ColumnFamilyRowMapper<String, String, Object> mapper = new
		// ColumnFamilyRowMapper<String, String, Object>() {
		// @Override
		// public String mapRow(ColumnFamilyResult<String, String> rs) {
		//
		// for (String columnName : rs.getColumnNames()) {
		// ByteBuffer bytes = rs.getColumn(columnName).getValueBytes();
		// if (columnName.equals(SPACE_DOCUMENT_NAME_KEY)) {
		// return StringSerializer.get().fromByteBuffer(bytes);
		// }
		// }
		//
		// return "Could not find column " + SPACE_DOCUMENT_NAME_KEY;
		// }
		// };
		// Object name = template.queryColumns(SPACEDOCUMENT_ID, mapper);
		// Assert.assertEquals(SPACEDOCUMENT_NAME, name);
	}

}
