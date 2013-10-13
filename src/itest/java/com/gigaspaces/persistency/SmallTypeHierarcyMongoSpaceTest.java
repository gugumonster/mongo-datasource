package com.gigaspaces.persistency;

import com.gigaspaces.itest.model.SmallTypeHierarcyCassandraSpaceDataClassA;
import com.gigaspaces.itest.model.SmallTypeHierarcyCassandraSpaceDataClassB;
import com.gigaspaces.itest.model.SmallTypeHierarcyCassandraSpaceDataClassC;

public class SmallTypeHierarcyMongoSpaceTest extends AbstractSystemTestUnit {

	@Override
	public void test() {
		SmallTypeHierarcyCassandraSpaceDataClassA typeA = new SmallTypeHierarcyCassandraSpaceDataClassA();
		SmallTypeHierarcyCassandraSpaceDataClassB typeB = new SmallTypeHierarcyCassandraSpaceDataClassB();
		SmallTypeHierarcyCassandraSpaceDataClassC typeC = new SmallTypeHierarcyCassandraSpaceDataClassC();

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
		
		SmallTypeHierarcyCassandraSpaceDataClassA[] as = gigaSpace.readMultiple(new SmallTypeHierarcyCassandraSpaceDataClassA());
	    clearMemory(gigaSpace);
	    SmallTypeHierarcyCassandraSpaceDataClassB[] bs = gigaSpace.readMultiple(new SmallTypeHierarcyCassandraSpaceDataClassB());
	    clearMemory(gigaSpace);
	    SmallTypeHierarcyCassandraSpaceDataClassC[] cs = gigaSpace.readMultiple(new SmallTypeHierarcyCassandraSpaceDataClassC());
	
        assertExpectedQueryResult(as, typeA, typeB, typeC);
        assertExpectedQueryResult(bs, typeB, typeC);
        assertExpectedQueryResult(cs, typeC);
	}
	
	private void assertExpectedQueryResult(Object[] actual, Object ... expected) {
        //Assert.assertEquivalenceArrays("Read result", expected, actual);
    }	
}
