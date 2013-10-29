/*******************************************************************************
 * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.gigaspaces.persistency.parser;

import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class SQL2MongoBaseVisitor<T> extends AbstractParseTreeVisitor<T>
		implements SQL2MongoVisitor<T> {

	private DBObject query;

	Stack<String> stack = new Stack<String>();

	QueryBuilder atom = QueryBuilder.start();
	LinkedList<DBObject> ands = new LinkedList<DBObject>();
	LinkedList<DBObject> ors = new LinkedList<DBObject>();

	public SQL2MongoBaseVisitor() {
		this.query = new BasicDBObject();
	}

	public T visitNot(SQL2MongoParser.NotContext ctx) {
		return visitChildren(ctx);
	}

	public DBObject getQuery() {
		return query;
	}

	public T visitExpression(SQL2MongoParser.ExpressionContext ctx) {
		return visitChildren(ctx);
	}

	public T visitAtom(SQL2MongoParser.AtomContext ctx) {

		if (ctx.getChildCount() > 0) {
			String id = ctx.getChild(0).getText();

			stack.push(id);
		}

		return visitChildren(ctx);
	}

	public T visitOp(SQL2MongoParser.OpContext ctx) {
		String op = ctx.getText();

		stack.push(op);

		return visitChildren(ctx);
	}

	public T visitOr(SQL2MongoParser.OrContext ctx) {

		T r = visitChildren(ctx);

		if (IsLogic(ctx, "OR")) {

			QueryBuilder q = QueryBuilder.start();

			for (DBObject at : ands)
				q.or(at);

			Set<String> keys = atom.get().keySet();

			for (String key : keys) {
				q.or(new BasicDBObject(key, atom.get().get(key)));
			}

			ands.clear();
			atom = QueryBuilder.start();
			ors.add(q.get());
		}

		return r;
	}

	public T visitValue(SQL2MongoParser.ValueContext ctx) {

		T r = visitChildren(ctx);

		String op = stack.pop();
		String id = stack.pop();

		String val = ctx.getChild(0).getText();

		atom.and(id);

		if ("is".equals(op)) {
			buildIsExpression(val, atom);
		} else if ("like".equals(op)) {
			buildLikeExpression(val, atom);
		} else if ("rlike".equals(op)) {
			atom.regex(Pattern.compile(val));
		} else if ("!=".equals(op)) {
			atom.notEquals(evaluate(val));
		} else if ("=".equals(op)) {
			atom.is(evaluate(val));
		} else if (">=".equals(op)) {
			atom.greaterThanEquals(evaluate(val));
		} else if ("<=".equals(op)) {
			atom.lessThanEquals(evaluate(val));
		} else if ("<".equals(op)) {
			atom.lessThan(evaluate(val));
		} else if (">".equals(op)) {
			atom.greaterThan(evaluate(val));
		}

		return r;
	}

	/**
	 * Evaluate string presentation to object
	 * 
	 * @param val
	 *            - string presentation of value
	 * @return Object instance type
	 */
	private Object evaluate(String val) {
		
		if(val == null || val.isEmpty())
			return null;
		
		if(val.equals("?"))
			return "'%{}'";
		
		boolean isValue = val.matches("'[^']*'");
		// test if value is String
		if (isValue) {

			return val.substring(1, val.length() - 1);
		}

		// test if value is true | false
		isValue = val.matches("(true|false)");

		if (isValue) {
			return Boolean.parseBoolean(val);
		}

		// test if number value
		isValue = val.matches("[0-9\\.]+");

		if (isValue) {
			int floatIndex = val.indexOf('.');

			if (floatIndex > -1) {
				return Double.parseDouble(val);
			} else {
				return Long.parseLong(val);
			}
		}

		throw new IllegalArgumentException(val);
	}

	public T visitParse(SQL2MongoParser.ParseContext ctx) {

		T r = visitChildren(ctx);

		for (DBObject o : ors) {
			query.putAll(o);
		}

		for (DBObject o : ands)
			query.putAll(o);

		DBObject o = atom.get();

		if (o.keySet().size() > 0)
			query.putAll(o);

		return r;
	}

	public T visitAnd(SQL2MongoParser.AndContext ctx) {

		T r = visitChildren(ctx);

		if (IsLogic(ctx, "AND")) {

			ands.add(atom.get());

			atom = QueryBuilder.start();
		}

		return r;
	}

	private boolean IsLogic(ParserRuleContext ctx, String text) {

		for (int i = 0; i < ctx.getChildCount(); i++) {
			ParseTree c = ctx.getChild(i);

			if (c.getText().equals(text))
				return true;
		}
		return false;
	}

	private void buildIsExpression(String val, QueryBuilder subQuery) {

		int index = val.indexOf("NOT");

		subQuery.exists(!(index > -1));
	}

	private void buildLikeExpression(String val, QueryBuilder subQuery) {
		if (val != null && !val.isEmpty()) {
			if (val.charAt(0) == '\'')
				val = val.substring(1);

			if (val.charAt(val.length() - 1) == '\'')
				val = val.substring(0, val.length() - 1);

			if (val.charAt(0) != '%')
				val = "^" + val;
			else
				val = val.substring(1);

			if (val.charAt(val.length() - 1) != '%')
				val = val + "$";
			else
				val = val.substring(0, val.length() - 1);

			val = val.replaceAll("%", ".*");
			val = val.replace('_', '.');

			subQuery.regex(Pattern.compile(val));
		}
	}
}