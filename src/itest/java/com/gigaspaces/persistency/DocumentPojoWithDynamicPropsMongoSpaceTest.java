package com.gigaspaces.persistency;

import junit.framework.Assert;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.itest.model.TestDataTypeWithDynamicProps;
import com.gigaspaces.itest.model.TestDataTypeWithDynamicPropsDocument;
import com.gigaspaces.itest.model.TestDataTypeWithDynamicPropsPojo;
import com.gigaspaces.itest.model.TestDataTypeWithDynamicPropsUtils;


public class DocumentPojoWithDynamicPropsMongoSpaceTest extends
		AbstractSystemTestUnit {

	@Override
	public void test() {
		testWritePojoReadPojo();
		testWriteDocumentReadPojo();
		testWriteDocumentReadDocument();
		testWritePojoReadDocument();

	}


    private void testWritePojoReadPojo() {
        test(new TestDescriptor() {
            public TestDataTypeWithDynamicProps getWritten() {
                return new TestDataTypeWithDynamicPropsPojo();
            }
            public TestDataTypeWithDynamicProps getTemplate() {
                return new TestDataTypeWithDynamicPropsPojo();
            }
        });
    }
    
    private void testWriteDocumentReadPojo() {
        test(new TestDescriptor() {
            public TestDataTypeWithDynamicProps getWritten() {
                return new TestDataTypeWithDynamicPropsDocument();
            }
            public TestDataTypeWithDynamicProps getTemplate() {
                return new TestDataTypeWithDynamicPropsPojo();
            }
        });
    }
    
    private void testWritePojoReadDocument() {
        test(new TestDescriptor() {
            public TestDataTypeWithDynamicProps getWritten() {
                return new TestDataTypeWithDynamicPropsPojo();
            }
            public TestDataTypeWithDynamicProps getTemplate() {
                return new TestDataTypeWithDynamicPropsDocument();
            }
        });
    }
    
    private void testWriteDocumentReadDocument() {
        test(new TestDescriptor() {
            public TestDataTypeWithDynamicProps getWritten() {
                return new TestDataTypeWithDynamicPropsDocument();
            }
            public TestDataTypeWithDynamicProps getTemplate() {
                return new TestDataTypeWithDynamicPropsPojo();
            }
        });
    }

	private void test(TestDescriptor descriptor) {
		TestDataTypeWithDynamicProps data = descriptor.getWritten();
		TestDataTypeWithDynamicPropsUtils.populateAllProperties(data);
		gigaSpace.write(data);
		waitForEmptyReplicationBacklogAndClearMemory(gigaSpace);
		// read multiple to ensure we hit cassandra
		Object[] readDataArr = gigaSpace.readMultiple(descriptor.getTemplate());
		Object readData = readDataArr[0];
		TestDataTypeWithDynamicProps typedReadData = null;
		// if (readData instanceof TestDataTypeWithDynamicProps)
		// typedReadData = (TestDataTypeWithDynamicProps) readData;
		// else
		// // this could only be SpaceDocument
		// typedReadData = (TestDataTypeWithDynamicProps) converter
		// .toObject((SpaceDocument) readData);
		TestDataTypeWithDynamicPropsUtils.assertTestDataEquals(data,
				typedReadData);
		gigaSpace.clear(descriptor.getTemplate());
		waitForEmptyReplicationBacklogAndClearMemory(gigaSpace);
		Assert.assertEquals("unexpected entries", 0,
				gigaSpace.count(descriptor.getTemplate()));
	}

	interface TestDescriptor {
		TestDataTypeWithDynamicProps getWritten();

		TestDataTypeWithDynamicProps getTemplate();
	}
}
