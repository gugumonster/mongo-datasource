package com.gigaspaces.persistency;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceProperty;

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

	@SpaceClass
	public static class SmallTypeHierarcyCassandraSpaceDataClassA {

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((aProp == null) ? 0 : aProp.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SmallTypeHierarcyCassandraSpaceDataClassA other = (SmallTypeHierarcyCassandraSpaceDataClassA) obj;
			if (aProp == null) {
				if (other.aProp != null)
					return false;
			} else if (!aProp.equals(other.aProp))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		private String id;
		private String aProp;

		@SpaceId(autoGenerate = true)
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		@SpaceProperty
		public String getAProp() {
			return aProp;
		}

		public void setAProp(String aProp) {
			this.aProp = aProp;
		}

	}

	@SpaceClass
	public static class SmallTypeHierarcyCassandraSpaceDataClassB extends
			SmallTypeHierarcyCassandraSpaceDataClassA {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((bProp == null) ? 0 : bProp.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			SmallTypeHierarcyCassandraSpaceDataClassB other = (SmallTypeHierarcyCassandraSpaceDataClassB) obj;
			if (bProp == null) {
				if (other.bProp != null)
					return false;
			} else if (!bProp.equals(other.bProp))
				return false;
			return true;
		}

		private String bProp;

		@SpaceProperty
		public String getBProp() {
			return bProp;
		}

		public void setBProp(String bProp) {
			this.bProp = bProp;
		}
	}

	@SpaceClass
	public static class SmallTypeHierarcyCassandraSpaceDataClassC extends
			SmallTypeHierarcyCassandraSpaceDataClassB {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((cProp == null) ? 0 : cProp.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			SmallTypeHierarcyCassandraSpaceDataClassC other = (SmallTypeHierarcyCassandraSpaceDataClassC) obj;
			if (cProp == null) {
				if (other.cProp != null)
					return false;
			} else if (!cProp.equals(other.cProp))
				return false;
			return true;
		}

		private String cProp;

		@SpaceProperty
		public String getCProp() {
			return cProp;
		}

		public void setCProp(String cProp) {
			this.cProp = cProp;
		}
	}
}
