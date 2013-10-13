package com.gigaspaces.itest.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceProperty;

@SpaceClass
public class SmallTypeHierarcyCassandraSpaceDataClassB extends
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
