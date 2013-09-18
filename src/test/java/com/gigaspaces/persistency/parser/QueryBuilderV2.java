package com.gigaspaces.persistency.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class QueryBuilderV2 {

	@Test
	public void andTest() {
		
		DBObject [] ands = new DBObject[]{
			new BasicDBObject("x","6"),
			new BasicDBObject("y","7"),
			new BasicDBObject("z","18")
		};
		
		QueryBuilder qb=
				QueryBuilder.start().and(ands[0]).or(ands[2]).and(ands[1]).or(ands[2]);
		
		DBObject q1 = qb.get();
		
		fail("Not yet implemented");
	}

}
