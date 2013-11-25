package com.gigaspaces.persistency.utest.metadata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import com.gigaspaces.stest.model.Priority;
import com.gigaspaces.stest.model.TestDataTypeWithDynamicPropsPojo;
import com.gigaspaces.stest.model.TestDataTypeWithDynamicPropsUtils;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.internal.utils.Assert;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.metadata.MongoDocumentObjectConverter;
import com.gigaspaces.persistency.metadata.SpaceDocumentMapperImpl;
import com.mongodb.DBObject;

public class DocumentSpacePojoTest {

	private SpaceDocumentMapperImpl converter = new SpaceDocumentMapperImpl(createMockedSpaceTypeDescriptor());

	private static final String TEST_TYPE_NAME = "TestType";
	private static final String KEY_NAME = "keyField";
	private static final UUID KEY_VAL = UUID.randomUUID();
	private static final String LONG_FIELD = "longField";
	private static final Long LONG_FIELD_VAL = new Long(888L);
	private static final String STRING_FIELD = "stringField";
	private static final String STRING_FIELD_VAL = "testString";
	private static final String BIGINT_FIELD = "bigintField";
	private static final BigInteger BIGINT_FIELD_VAL = new BigInteger("12323");
	private static final String BIGDECIMAL_FIELD = "bigDecimalField";
	private static final BigDecimal BIGDECIMAL_FIELD_VAL = new BigDecimal(
			new BigInteger("123123"));
	private static final String DYNAMIC_FIELD_1 = "dynamicFIELD1";
	private static final Integer DYNAMIC_FIELD_1_VAL = new Integer(3333);
	private static final String DYNAMIC_FIELD_2 = "dynamicFIELD2";
	private static final Date DYNAMIC_FIELD_2_VAL = new Date(1234);
	private static final String DYNAMIC_FIELD_3 = "dynamicFIELD3";
	private static final Float DYNAMIC_FIELD_3_VAL = new Float(3434.6f);
	private static final String DYNAMIC_FIELD_4 = "dynamicFIELD4";
	private static final Double DYNAMIC_FIELD_4_VAL = new Double(123.5);
	private static final String DYNAMIC_FIELD_5 = "dynamicFIELD5";
	private static final Boolean DYNAMIC_FIELD_5_VAL = Boolean.TRUE;
	private static final String DYNAMIC_FIELD_6 = "dynamicFIELD6";
	private static final Byte DYNAMIC_FIELD_6_VAL = Byte.valueOf((byte) 1);
	private static final String DYNAMIC_FIELD_7 = "dynamicFIELD7";
	private static final Character DYNAMIC_FIELD_7_VAL = Character.valueOf('a');
	private static final String DYNAMIC_FIELD_8 = "dynamicFIELD8";
	private static final byte[] DYNAMIC_FIELD_8_VAL = { (byte) 123 };

	@SuppressWarnings("unused")
	private static class PojoTestType {

		private UUID key = UUID.randomUUID();
		private Long longField = new Long(888L);
		private String stringField = "testString";
		private BigInteger bigIntegerField = new BigInteger("12323");
		private BigDecimal bigDecimalField = new BigDecimal("123123");
		private Integer integerField = new Integer(3333);
		private Date dateField = new Date(1234);
		private Float floatField = new Float(3434.6f);
		private Double doubleField = new Double(123.5);
		private Boolean booleanField = Boolean.TRUE;
		private Byte byteField = Byte.valueOf((byte) 1);
		private Character characterField = Character.valueOf('a');
		private byte[] byteArrayField = { (byte) 123 };

		public PojoTestType() {
		}

		public UUID getKey() {
			return key;
		}

		public Long getLongField() {
			return longField;
		}

		public String getStringField() {
			return stringField;
		}

		public BigInteger getBigIntegerField() {
			return bigIntegerField;
		}

		public BigDecimal getBigDecimalField() {
			return bigDecimalField;
		}

		public Integer getIntegerField() {
			return integerField;
		}

		public Date getDateField() {
			return dateField;
		}

		public Float getFloatField() {
			return floatField;
		}

		public Double getDoubleField() {
			return doubleField;
		}

		public Boolean getBooleanField() {
			return booleanField;
		}

		public Byte getByteField() {
			return byteField;
		}

		public Character getCharacterField() {
			return characterField;
		}

		public byte[] getByteArrayField() {
			return byteArrayField;
		}

		public void setKey(UUID key) {
			this.key = key;
		}

		public void setLongField(Long longField) {
			this.longField = longField;
		}

		public void setStringField(String stringField) {
			this.stringField = stringField;
		}

		public void setBigIntegerField(BigInteger bigIntegerField) {
			this.bigIntegerField = bigIntegerField;
		}

		public void setBigDecimalField(BigDecimal bigDecimalField) {
			this.bigDecimalField = bigDecimalField;
		}

		public void setIntegerField(Integer integerField) {
			this.integerField = integerField;
		}

		public void setDateField(Date dateField) {
			this.dateField = dateField;
		}

		public void setFloatField(Float floatField) {
			this.floatField = floatField;
		}

		public void setDoubleField(Double doubleField) {
			this.doubleField = doubleField;
		}

		public void setBooleanField(Boolean booleanField) {
			this.booleanField = booleanField;
		}

		public void setByteField(Byte byteField) {
			this.byteField = byteField;
		}

		public void setCharacterField(Character characterField) {
			this.characterField = characterField;
		}

		public void setByteArrayField(byte[] byteArrayField) {
			this.byteArrayField = byteArrayField;
		}

	}

	@Test
	public void testBasicPojo() {
		PojoTestType pojo = new PojoTestType();

		DBObject bson = converter.toDBObject(pojo);

		System.out.println(bson);

		PojoTestType pojo1 = (PojoTestType) converter.toDocument(bson);

		Assert.isTrue(pojo.getKey().equals(pojo1.getKey()));
		Assert.isTrue(pojo.getStringField().equals(pojo1.getStringField()));
		Assert.isTrue(pojo.getLongField().equals(pojo1.getLongField()));
		Assert.isTrue(pojo.getBigIntegerField().equals(
				pojo1.getBigIntegerField()));
		Assert.isTrue(pojo.getBigDecimalField().equals(
				pojo1.getBigDecimalField()));
		Assert.isTrue(pojo.getDateField().equals(pojo1.getDateField()));
		Assert.isTrue(pojo.getDoubleField().equals(pojo1.getDoubleField()));
		Assert.isTrue(pojo.getIntegerField().equals(pojo1.getIntegerField()));
		Assert.isTrue(pojo.getFloatField().equals(pojo1.getFloatField()));
		Assert.isTrue(pojo.getCharacterField()
				.equals(pojo1.getCharacterField()));
		Assert.isTrue(pojo.getBooleanField().equals(pojo1.getBooleanField()));
		Assert.isTrue(pojo.getByteField().equals(pojo1.getByteField()));
		Assert.isTrue(pojo.getByteArrayField()
				.equals(pojo1.getByteArrayField()));
	}

	private SpaceTypeDescriptor createMockedSpaceTypeDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Test
	public void testBasicDocument() {

		SpaceDocument spaceDoc = new SpaceDocument(TEST_TYPE_NAME)
				.setProperty(KEY_NAME, KEY_VAL)
				.setProperty(STRING_FIELD, STRING_FIELD_VAL)
				.setProperty(LONG_FIELD, LONG_FIELD_VAL)
				.setProperty(BIGINT_FIELD, BIGINT_FIELD_VAL)
				.setProperty(BIGDECIMAL_FIELD, BIGDECIMAL_FIELD_VAL)
				.setProperty(DYNAMIC_FIELD_1, DYNAMIC_FIELD_1_VAL)
				.setProperty(DYNAMIC_FIELD_2, DYNAMIC_FIELD_2_VAL)
				.setProperty(DYNAMIC_FIELD_3, DYNAMIC_FIELD_3_VAL)
				.setProperty(DYNAMIC_FIELD_4, DYNAMIC_FIELD_4_VAL)
				.setProperty(DYNAMIC_FIELD_5, DYNAMIC_FIELD_5_VAL)
				.setProperty(DYNAMIC_FIELD_6, DYNAMIC_FIELD_6_VAL)
				.setProperty(DYNAMIC_FIELD_7, DYNAMIC_FIELD_7_VAL)
				.setProperty(DYNAMIC_FIELD_8, DYNAMIC_FIELD_8_VAL);

		DBObject bson = converter.toDBObject(spaceDoc);

		System.out.println(bson);

		SpaceDocument spaceDocument2 = (SpaceDocument) converter
				.toDocument(bson);

		assertSpaceDocument(KEY_NAME, spaceDoc, spaceDocument2);
		assertSpaceDocument(STRING_FIELD, spaceDoc, spaceDocument2);
		assertSpaceDocument(LONG_FIELD, spaceDoc, spaceDocument2);
		assertSpaceDocument(BIGINT_FIELD, spaceDoc, spaceDocument2);
		assertSpaceDocument(BIGDECIMAL_FIELD, spaceDoc, spaceDocument2);
		assertSpaceDocument(DYNAMIC_FIELD_1, spaceDoc, spaceDocument2);
		assertSpaceDocument(DYNAMIC_FIELD_2, spaceDoc, spaceDocument2);
		assertSpaceDocument(DYNAMIC_FIELD_3, spaceDoc, spaceDocument2);
		assertSpaceDocument(DYNAMIC_FIELD_4, spaceDoc, spaceDocument2);
		assertSpaceDocument(DYNAMIC_FIELD_5, spaceDoc, spaceDocument2);
		assertSpaceDocument(DYNAMIC_FIELD_6, spaceDoc, spaceDocument2);
		//assertSpaceDocument(DYNAMIC_FIELD_7, spaceDoc, spaceDocument2);
		assertSpaceDocument(DYNAMIC_FIELD_8, spaceDoc, spaceDocument2);

	}

	public void assertSpaceDocument(String property, SpaceDocument expected,
			SpaceDocument actual) {
		Object expected1 = expected.getProperty(property);
		Object actual1 = actual.getProperty(property);

		Assert.isTrue(expected1.equals(actual1));
	}

	@Test
	public void testDynamicPropertiesPojo() {

		TestDataTypeWithDynamicPropsPojo data1 = new TestDataTypeWithDynamicPropsPojo();

		TestDataTypeWithDynamicPropsUtils.populateAllProperties(data1);

		SpaceDocument doc1 = MongoDocumentObjectConverter.instance()
				.toSpaceDocument(data1);

		DBObject bson = converter.toDBObject(doc1);

		System.out.println(bson);

		TestDataTypeWithDynamicPropsPojo data2 = (TestDataTypeWithDynamicPropsPojo) converter
				.toDocument(bson);

		TestDataTypeWithDynamicPropsUtils.assertTestDataEquals(data1, data2);
	}

	@Test
	public void testEnum() {

		SpaceDocument doc = new SpaceDocument("enumPropertyType");

		doc.setProperty("enumProperty", Priority.MAJOR);

		DBObject bson = converter.toDBObject(doc);

		DBObject e = (DBObject) bson.get("enumProperty");

		Priority priority = (Priority) converter.fromDBObject(e);

		Assert.isTrue(Priority.MAJOR == priority);

	}

}
