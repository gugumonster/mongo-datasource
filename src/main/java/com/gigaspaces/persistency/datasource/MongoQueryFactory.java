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
package com.gigaspaces.persistency.datasource;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import com.gigaspaces.datasource.DataSourceQuery;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.parser.SQL2MongoBaseVisitorV3;
import com.gigaspaces.persistency.parser.SQL2MongoLexer;
import com.gigaspaces.persistency.parser.SQL2MongoParser;
import com.gigaspaces.persistency.parser.SQL2MongoParser.ParseContext;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class MongoQueryFactory {

	private static final String PARAM_PLACEHOLDER = "\"'%{}'\"";
	private static final Map<SpaceTypeDescriptor, Map<String, StringBuilder>> cachedQuery = new HashMap<SpaceTypeDescriptor, Map<String, StringBuilder>>();

	public synchronized static DBObject create(DataSourceQuery sql) {
		Map<String, StringBuilder> b = cachedQuery.get(sql.getTypeDescriptor());

		if (b == null) {
			b = new HashMap<String, StringBuilder>();

			cachedQuery.put(sql.getTypeDescriptor(), b);
		}

		String q = sql.getAsSQLQuery().getQuery();

		StringBuilder sb = b.get(q);

		if (sb == null) {
			sb = parse(q);

			b.put(q, sb);
		}

		DBObject qResult = bind(sb, sql.getAsSQLQuery().getQueryParameters());

		replaceIdProperty(qResult, sql.getTypeDescriptor());

		return qResult;
	}

	private static void replaceIdProperty(DBObject qResult,
			SpaceTypeDescriptor typeDescriptor) {

		if (qResult.containsField(typeDescriptor.getIdPropertyName())) {

			Object value = qResult.get(typeDescriptor.getIdPropertyName());

			qResult.put("_id", value);

			qResult.removeField(typeDescriptor.getIdPropertyName());
		}

	}

	private static StringBuilder parse(String sql) {
		ANTLRInputStream charstream = new ANTLRInputStream(sql);

		SQL2MongoLexer lexer = new SQL2MongoLexer(charstream);

		TokenStream tokenStream = new CommonTokenStream(lexer);

		SQL2MongoParser parser = new SQL2MongoParser(tokenStream);

		SQL2MongoBaseVisitorV3<ParseContext> visitor = new SQL2MongoBaseVisitorV3<ParseContext>();

		parser.parse().accept(visitor);

		return new StringBuilder(visitor.getQuery().toString());
	}

	public static DBObject bind(StringBuilder sb, Object[] parameters) {

		StringBuilder sb1 = new StringBuilder(sb.toString());
		
		if (parameters != null) {
			for (int i = 0, j = 0; i < parameters.length; i++) {

				j = sb1.indexOf(PARAM_PLACEHOLDER, j);

				String p = serialize(parameters[i]);

				sb1.replace(j, j + PARAM_PLACEHOLDER.length(), p);
			}
		}
		
		DBObject q = (DBObject) JSON.parse(sb1.toString());

		return q;
	}

	private static String serialize(Object parameter) {

		if (parameter.getClass().isEnum()) {
			return "\"" + parameter + "\"";
		}

		return JSON.serialize(parameter);
	}
}
