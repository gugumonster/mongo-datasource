package com.gigaspaces.persistency.parser;

import static org.junit.Assert.assertEquals;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Test;

import com.gigaspaces.persistency.parser.SQL2MongoParser.ParseContext;

public class SQL2MongoBaseVisitorV1Test {

	@Test
	public void testGreaterThanAndEquals() {

		String line = "num>=100";

		String expected = "{num:{$gte:100}}";
		String acual = parse(line);
		assertEquals(expected, acual);
	}

	public void testGreaterThan() {

		String line = "num>100";

		String expected = "{num:{$gt:100}}";
		String acual = parse(line);

		assertEquals(expected, acual);

	}

	@Test
	public void testGreaterSmallerThan() {
		String line = "num<100";
		String actual = parse(line);
		String expected = "{num:{$lt:100}}";
		assertEquals(expected, actual);
	}

	@Test
	public void testLessAndEquals() {
		String line = "num<=100";
		String actual = parse(line);
		String expected = "{num:{$lte:100}}";
		assertEquals(expected, actual);
	}

	@Test
	public void testnotEquals() {
		String line = "num<>100";
		String actual = parse(line);
		String expected = "{num:{$ne:100}}";
		assertEquals(expected, actual);
	}

	@Test
	public void testAnd() {
		String line = "num >= 1 AND num <= 100";
		String actual = parse(line).replaceAll(" ","");
		
		// db.inventory.find({ $and: [ { price: 1.99 }, { qty: { $lt: 20 } }, { sale: true } ] } )
		String expected = "{$and:[{num:{$gte:1}},{num:{$lte:100}}]}";
		assertEquals(expected, actual);
	}
	@Test
	public void testOr() {
		String line = "num >= 1 OR num <= 100";
		String actual = parse(line).replaceAll(" ","");
		
		// db.inventory.find({ $and: [ { price: 1.99 }, { qty: { $lt: 20 } }, { sale: true } ] } )
		String expected = "{$or:[{num:{$gte:1}},{num:{$lte:100}}]}";
		assertEquals(expected, actual);
	}
	
         
         
                     
	
	

	private String parse(String sqlQuery) {
		ANTLRInputStream charstream = new ANTLRInputStream(sqlQuery);

		SQL2MongoLexer lexer = new SQL2MongoLexer(charstream);

		TokenStream tokenStream = new CommonTokenStream(lexer);

		SQL2MongoParser parser = new SQL2MongoParser(tokenStream);

		StringBuilder sb = new StringBuilder();

		parser.parse().accept(new SQL2MongoBaseVisitorV1<ParseContext>(sb));
		return sb.toString();
	}
}
