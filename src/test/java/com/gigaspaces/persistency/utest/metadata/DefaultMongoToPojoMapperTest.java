package com.gigaspaces.persistency.utest.metadata;
//package com.gigaspaces.persistency.metadata;
//
//import static org.junit.Assert.*;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.gigaspaces.document.SpaceDocument;
//import com.gigaspaces.metadata.SpaceTypeDescriptor;
//import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
//import com.mongodb.BasicDBObject;
//
//public class DefaultMongoToPojoMapperTest {
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}
//
//	private DefaultMongoToPojoMapper mapper;
//
//	class TestPojo {
//		private String stringField;
//
//		public String getStringField() {
//			return stringField;
//		}
//
//		public void setStringField(String stringField) {
//			this.stringField = stringField;
//		}
//	}
//
//	@Before
//	public void setUp() throws Exception {
//
//		SpaceTypeDescriptorBuilder builder = new SpaceTypeDescriptorBuilder(
//				TestPojo.class, null);
//
//		SpaceTypeDescriptor spaceTypeDescriptor = builder.create();
//
//		mapper = new DefaultMongoToPojoMapper(spaceTypeDescriptor);
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	@Test
//	public void testMaps() {
//		BasicDBObject bson = new BasicDBObject("name", "amir");
//
//		SpaceDocument obj = (SpaceDocument) mapper.maps(bson);
//
//		assertEquals("amir", obj.getProperty("name"));
//	}
//
//}
