package com.gigaspaces.itest.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class IssuePojo implements Comparable<IssuePojo> {

	private Integer id;
	private String name;
	
	public IssuePojo() {}
	
	public IssuePojo(Integer id, String name) {
		this.id= id;
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

	public int compareTo(IssuePojo o) {
		
		if(id == o.id)
			return 0;
		
		return id - o.id;	
	}

	

}
