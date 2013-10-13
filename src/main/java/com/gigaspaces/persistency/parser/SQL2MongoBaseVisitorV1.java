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

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class SQL2MongoBaseVisitorV1<T> extends AbstractParseTreeVisitor<T>
		implements SQL2MongoVisitor<T> {

	private final Map<String, String> map = new HashMap<String, String>();

	private int expr = 0;
	private int and = -1;
	private int or = -1;
	private int operator = 0;	

	private StringBuilder query;

	public SQL2MongoBaseVisitorV1(StringBuilder sb) {
		map.put(">", "$gt");
		map.put(">=", "$gte");
		map.put("<", "$lt");
		map.put("<=", "$lte");
		map.put("<>", "$ne");
		this.query = sb;
	}

	public T visitNot(SQL2MongoParser.NotContext ctx) {

		and--;

		if (and == -1)
			query.append("]}");

		return visitChildren(ctx);
	}

	public T visitExpression(SQL2MongoParser.ExpressionContext ctx) {

		return visitChildren(ctx);
	}

	public T visitAtom(SQL2MongoParser.AtomContext ctx) {
		expr++;

		if (expr > 1)
			query.append(",");

		query.append("{");

		if (ctx.getChildCount() == 3) {
			String id = ctx.getChild(0).getText();

			query.append(id);
			query.append(':');
		}

		T r = visitChildren(ctx);

		query.append("}");

		return r;
	}

	public T visitOp(SQL2MongoParser.OpContext ctx) {
		String op = map.get(ctx.getText());

		if (op != null) {
			query.append('{');
			query.append(op);
			query.append(':');
			operator++;
		}

		T r = visitChildren(ctx);

		return r;
	}

	public T visitOr(SQL2MongoParser.OrContext ctx) {

		if (or == -1 && IsLogic(ctx, "OR")) {
			or = ctx.and().size();
			query.append("{$or : [");
		}

		T r = visitChildren(ctx);

		if (or == 0 || and == 0) {
			query.append("]}");
		}

		return r;
	}

	public T visitValue(SQL2MongoParser.ValueContext ctx) {

		if ("?".equals(ctx.getText())) {
			query.append("\"?\"");
		} else
			query.append(ctx.getText());

		if (operator > 0) {
			operator--;
			query.append('}');
		}

		return visitChildren(ctx);
	}

	public T visitParse(SQL2MongoParser.ParseContext ctx) {

		return visitChildren(ctx);
	}

	public T visitAnd(SQL2MongoParser.AndContext ctx) {

		or--;

		if (and == -1 && IsLogic(ctx, "AND")) {
			and = ctx.not().size();
			query.append("{$and : [");
		}

		T r = visitChildren(ctx);

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
}