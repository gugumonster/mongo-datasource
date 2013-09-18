package com.gigaspaces.persistency.parser;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Test;

import com.gigaspaces.persistency.parser.SQL2MongoParser.ParseContext;
import com.mongodb.QueryBuilder;

public class SQL2MongoBaseVisitorV3Test {

	@Test
	public void testSQL2MongoBaseVisitorV3() {
		// fail("Not yet implemented");

	}

	@Test
	public void testEquals() {
		SQL2MongoBaseVisitorV3<ParseContext> visitor = parse("number = 10");

		QueryBuilder qb = QueryBuilder.start("number").is(10);

		assertEquals(qb.get(), visitor.getQuery());

	}

	@Test
	public void testNotEquals() {
		SQL2MongoBaseVisitorV3<ParseContext> visitor = parse("number != 100.0276");

		QueryBuilder qb = QueryBuilder.start("number").notEquals(100.0276);

		assertEquals(qb.get(), visitor.getQuery());
	}

	@Test
	public void testSimpleAnd() {
		SQL2MongoBaseVisitorV3<ParseContext> visitor = parse("number is NOT null AND name like '%m_n%db%'");

		QueryBuilder qb = QueryBuilder.start().and("number").exists(false)
				.and("name").regex(Pattern.compile("m.n.*db"));

		assertEquals(qb.get(), visitor.getQuery());
	}

	@Test
	public void testMultipleAnd() {
		SQL2MongoBaseVisitorV3<ParseContext> visitor = parse("number is NOT null AND name like '%m_n%db%' AND age >= 20");

		QueryBuilder qb = QueryBuilder.start().and("number").exists(false)
				.and("name").regex(Pattern.compile("m.n.*db")).and("age")
				.greaterThanEquals(20);

		assertEquals(qb.get(), visitor.getQuery());
	}

	@Test
	public void testSimpleOr() {
		SQL2MongoBaseVisitorV3<ParseContext> visitor = parse("x < 3.3 OR y <= 20");

		QueryBuilder qb = QueryBuilder.start()
				.or(QueryBuilder.start("x").lessThan(3.3).get())
				.or(QueryBuilder.start("y").lessThanEquals(20).get());

		assertEquals(qb.get(), visitor.getQuery());
	}

	@Test
	public void testMultipleOr() {
		SQL2MongoBaseVisitorV3<ParseContext> visitor = parse("x < 3.3 OR y <= 20 OR z = true");

		QueryBuilder qb = QueryBuilder.start()
				.or(QueryBuilder.start("x").lessThan(3.3).get())
				.or(QueryBuilder.start("y").lessThanEquals(20).get())
				.or(QueryBuilder.start("z").is(true).get());

		assertEquals(qb.get(), visitor.getQuery());
	}

	@Test
	public void testAndOr() {
		SQL2MongoBaseVisitorV3<ParseContext> visitor = parse("x < 3.3 AND y <= 20 OR z = true");

		QueryBuilder qb = QueryBuilder.start().or(
				QueryBuilder.start().and("x").lessThan(3.3).and("y")
						.lessThanEquals(20).get(),
				QueryBuilder.start("z").is(true).get());

		assertEquals(qb.get(), visitor.getQuery());
	}
	
	@Test
	public void testOrWithTwoAndClause() {
		SQL2MongoBaseVisitorV3<ParseContext> visitor = parse("x < 3.3 AND y <= 20 OR z = true AND w like 'ab%'");

		QueryBuilder qb = QueryBuilder.start().or(
				QueryBuilder.start().and("x").lessThan(3.3).and("y")
						.lessThanEquals(20).get(),
				QueryBuilder.start("z").is(true)
				.and("w")
				.regex(Pattern.compile("^ab"))				
				.get());

		assertEquals(qb.get(), visitor.getQuery());
	}

	@Test
	public void testAndWithTwoOrClause() {
		SQL2MongoBaseVisitorV3<ParseContext> visitor = parse("x < 3.3 OR y <= 20 AND z = true OR w like 'ab%'");

		QueryBuilder qb = QueryBuilder.start().and(
				QueryBuilder.start().and("x").lessThan(3.3).and("y")
						.lessThanEquals(20).get(),
				QueryBuilder.start("z").is(true)
				.and("w")
				.regex(Pattern.compile("^ab"))				
				.get());

		assertEquals(qb.get(), visitor.getQuery());
	}

	/**
	 * @return
	 */
	private SQL2MongoBaseVisitorV3<ParseContext> parse(String line) {
		ANTLRInputStream charstream = new ANTLRInputStream(line);

		SQL2MongoLexer lexer = new SQL2MongoLexer(charstream);

		TokenStream tokenStream = new CommonTokenStream(lexer);

		SQL2MongoParser parser = new SQL2MongoParser(tokenStream);

		SQL2MongoBaseVisitorV3<ParseContext> visitor = new SQL2MongoBaseVisitorV3<ParseContext>();

		parser.parse().accept(visitor);
		return visitor;
	}

}
