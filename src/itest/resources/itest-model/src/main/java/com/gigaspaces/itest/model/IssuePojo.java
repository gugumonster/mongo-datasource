package com.gigaspaces.itest.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class IssuePojo implements Comparable<IssuePojo> {

	private Integer id;
	private String name;
	private String priority;

	public IssuePojo() {
	}

	public IssuePojo(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	@SpaceId
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public int compareTo(IssuePojo o) {

		if (id == o.id)
			return 0;

		return id - o.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!IssuePojo.class.equals(obj.getClass()))
			return false;

		if (this == obj)
			return true;

		IssuePojo obj1 = (IssuePojo) obj;

		if (!id.equals(obj1.id))
			return false;

		if (!name.equals(obj1.name))
			return false;

		if (!priority.equals(obj1.priority))
			return false;

		return true;
	}

}
