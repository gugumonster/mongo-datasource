package com.gigaspaces.persistency.qa.itest;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openspaces.itest.persistency.common.mock.MockDataSourceIdQuery;
import org.openspaces.itest.persistency.common.mock.MockDataSourceQuery;
import org.openspaces.itest.persistency.common.mock.MockDataSourceSqlQuery;
import org.openspaces.itest.persistency.common.mock.MockIntroduceTypeData;
import org.openspaces.itest.persistency.common.mock.MockOperationsBatchDataBuilder;

import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.datasource.DataSourceQuery;
import com.gigaspaces.datasource.DataSourceSQLQuery;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;

/**
 * @author Shadi Massalha
 * 
 *         mognodb collection name rules is [A-Za-z0-9._]
 * 
 *         java name rules [A-Za-z0-9_$.] will map $ sign to __d_s__
 */
public class InnerClassMongoTest extends AbstractMongoTest {

	private static final String UPDATE_INNER_CLASS = "update innerClass";
	private static final String WRITE_INNER_CLASS = "write innerClass";

	public static class InnerClass {
		private String id;
		private String name;

		public InnerClass() {

		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private static final String KEY_NAME = "id";
	private static final String KEY_VALUE = "innerClass1";
	private static final String STRING_COL = "name";

	@Test
	public void test() throws IOException {
		// test type introduction
		MockIntroduceTypeData introduceDataType = createMockIntroduceTypeData();

		_syncInterceptor.onIntroduceType(introduceDataType);

		MockOperationsBatchDataBuilder builder = new MockOperationsBatchDataBuilder();
		// write
		builder.write(createSpaceDocument(WRITE_INNER_CLASS), KEY_NAME);

		_syncInterceptor.onOperationsBatchSynchronization(builder.build());

		builder.clear();

		InnerClass data = (InnerClass) _dataSource
				.getById(new MockDataSourceIdQuery(introduceDataType
						.getTypeDescriptor(), KEY_VALUE));

		Assert.assertEquals(WRITE_INNER_CLASS, data.getName());

		// update
		builder.update(createSpaceDocument(UPDATE_INNER_CLASS), KEY_NAME);

		_syncInterceptor.onOperationsBatchSynchronization(builder.build());

		DataSourceSQLQuery sqlQuery = new MockDataSourceSqlQuery("id = ?",
				new Object[] { KEY_VALUE });

		DataSourceQuery dataSourceQuery = new MockDataSourceQuery(
				createMockSpaceTypeDescriptor(), sqlQuery, Integer.MAX_VALUE);

		DataIterator<Object> iterator = _dataSource
				.getDataIterator(dataSourceQuery);

		assertTrue("No result", iterator.hasNext());
		InnerClass result = (InnerClass) iterator.next();
		Assert.assertEquals(UPDATE_INNER_CLASS, result.getName());

		builder.clear();

		// delete
		builder.remove(createSpaceDocument(UPDATE_INNER_CLASS), KEY_NAME);

		_syncInterceptor.onOperationsBatchSynchronization(builder.build());

		DataIterator<Object> iterator1 = _dataSource.initialDataLoad();

		assertTrue("Count is not 0", !iterator1.hasNext());
	}

	private SpaceDocument createSpaceDocument(String value) {
		return new SpaceDocument(InnerClass.class.getName()).setProperty(
				KEY_NAME, KEY_VALUE).setProperty(STRING_COL, value);

	}

	private MockIntroduceTypeData createMockIntroduceTypeData() {
		SpaceTypeDescriptor typeDescriptor = createMockSpaceTypeDescriptor();
		return new MockIntroduceTypeData(typeDescriptor);
	}

	private SpaceTypeDescriptor createMockSpaceTypeDescriptor() {

		return new SpaceTypeDescriptorBuilder(InnerClass.class, null)
				.idProperty(KEY_NAME).create();
	}

}
