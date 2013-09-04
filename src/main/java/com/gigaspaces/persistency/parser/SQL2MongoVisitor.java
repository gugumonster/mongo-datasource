// Generated from SQL2Mongo.g4 by ANTLR 4.0
 package com.gigaspaces.persistency.parser; 
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public interface SQL2MongoVisitor<T> extends ParseTreeVisitor<T> {
	T visitGroupBy(SQL2MongoParser.GroupByContext ctx);

	T visitNot(SQL2MongoParser.NotContext ctx);

	T visitExpression(SQL2MongoParser.ExpressionContext ctx);

	T visitAtom(SQL2MongoParser.AtomContext ctx);

	T visitOrderBy(SQL2MongoParser.OrderByContext ctx);

	T visitOp(SQL2MongoParser.OpContext ctx);

	T visitOr(SQL2MongoParser.OrContext ctx);

	T visitValue(SQL2MongoParser.ValueContext ctx);

	T visitParse(SQL2MongoParser.ParseContext ctx);

	T visitAnd(SQL2MongoParser.AndContext ctx);
}