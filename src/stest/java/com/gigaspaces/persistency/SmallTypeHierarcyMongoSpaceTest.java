package com.gigaspaces.persistency;

import com.gigaspaces.persistency.utils.AssertUtils;
import com.gigaspaces.stest.model.SmallTypeHierarcyMongoDBSpaceDataClassA;
import com.gigaspaces.stest.model.SmallTypeHierarcyMongoDBSpaceDataClassB;
import com.gigaspaces.stest.model.SmallTypeHierarcyMongoDBSpaceDataClassC;

public class SmallTypeHierarcyMongoSpaceTest extends AbstractSystemTestUnit {

	@Override
	public void test() {
		SmallTypeHierarcyMongoDBSpaceDataClassA typeA = new SmallTypeHierarcyMongoDBSpaceDataClassA();
		SmallTypeHierarcyMongoDBSpaceDataClassB typeB = new SmallTypeHierarcyMongoDBSpaceDataClassB();
		SmallTypeHierarcyMongoDBSpaceDataClassC typeC = new SmallTypeHierarcyMongoDBSpaceDataClassC();

		typeA.setAProp("a");
		typeB.setAProp("a");
		typeB.setBProp("b");
		typeC.setAProp("a");
		typeC.setBProp("b");
		typeC.setCProp("c");

		gigaSpace.write(typeA);
		gigaSpace.write(typeB);
		gigaSpace.write(typeC);
		waitForEmptyReplicationBacklogAndClearMemory(gigaSpace);

		SmallTypeHierarcyMongoDBSpaceDataClassA[] as = gigaSpace
				.readMultiple(new SmallTypeHierarcyMongoDBSpaceDataClassA());
		clearMemory(gigaSpace);
		SmallTypeHierarcyMongoDBSpaceDataClassB[] bs = gigaSpace
				.readMultiple(new SmallTypeHierarcyMongoDBSpaceDataClassB());
		clearMemory(gigaSpace);
		SmallTypeHierarcyMongoDBSpaceDataClassC[] cs = gigaSpace
				.readMultiple(new SmallTypeHierarcyMongoDBSpaceDataClassC());

		assertExpectedQueryResult(as, typeA, typeB, typeC);
		assertExpectedQueryResult(bs, typeB, typeC);
		assertExpectedQueryResult(cs, typeC);
	}

	private void assertExpectedQueryResult(Object[] actual, Object... expected) {
		AssertUtils.assertEquivalenceArrays("Read result", expected, actual);
	}

	@Override
	protected String getPUJar() {		
		return "/all-in-cache-0.0.1-SNAPSHOT.jar";
	}
}
