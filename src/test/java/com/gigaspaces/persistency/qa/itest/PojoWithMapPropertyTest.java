package com.gigaspaces.persistency.qa.itest;

import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.document.SpaceDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.itest.persistency.common.data.TestPojoWithPrimitives;
import org.openspaces.itest.persistency.common.mock.MockOperationsBatchDataBuilder;

import java.util.HashMap;

public class PojoWithMapPropertyTest extends AbstractMongoTest {
	private final String keyName = "key";
	private final String dynamicPropertyName = "dynamic";
	private final String fixedPropertyName = "fixed";

	@Before
	public void before() {
		_syncInterceptor
				.onIntroduceType(createIntroduceTypeDataFromSpaceDocument(
						createDocument(), keyName));
		_dataSource.initialMetadataLoad();
	}

	@Test
	public void test() {
		SpaceDocument document = createDocument();
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("prop",null);
        map.put("prop2","val");

		document.setProperty(dynamicPropertyName, map);
		MockOperationsBatchDataBuilder builder = new MockOperationsBatchDataBuilder();
		builder.write(document, keyName);
		_syncInterceptor.onOperationsBatchSynchronization(builder.build());

		DataIterator<Object> iterator = _dataSource.initialDataLoad();
		Assert.assertTrue("no result", iterator.hasNext());
		SpaceDocument documentResult = (SpaceDocument) iterator.next();
		Assert.assertEquals("Bad result", document, documentResult);				
		Assert.assertFalse("unexpected result", iterator.hasNext());

		iterator.close();
	}

	private SpaceDocument createDocument() {

		return new SpaceDocument("MyType").setProperty(keyName, 1);
	}


}
