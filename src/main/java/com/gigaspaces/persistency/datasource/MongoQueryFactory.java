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
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.json.Json;
import com.gigaspaces.datasource.DataSourceQuery;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.metadata.AsyncSpaceDocumentMapper;
import com.gigaspaces.persistency.metadata.SpaceDocumentMapper;
import com.gigaspaces.persistency.parser.SQL2MongoBaseVisitor;
import com.gigaspaces.persistency.parser.SQL2MongoLexer;
import com.gigaspaces.persistency.parser.SQL2MongoParser;
import com.gigaspaces.persistency.parser.SQL2MongoParser.ParseContext;
import com.mongodb.QueryBuilder;

/**
 * @author Shadi Massalha
 * 
 */
public class MongoQueryFactory {

	private static final String $REGEX = "$regex";
	private static final String LIKE = "like()";
	private static final String RLIKE = "rlike()";
	private static final String PARAM_PLACEHOLDER = "'%{}'";
	private static final Map<SpaceTypeDescriptor, Map<String, StringBuilder>> cachedQuery = new ConcurrentHashMap<SpaceTypeDescriptor, Map<String, StringBuilder>>();

	public static DocumentBuilder create(DataSourceQuery sql) {
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

		DocumentBuilder qResult = bind(sb, sql.getAsSQLQuery()
				.getQueryParameters(), sql.getTypeDescriptor());

		replaceIdProperty(qResult, sql.getTypeDescriptor());

		return qResult;
	}

	private static void replaceIdProperty(DocumentBuilder qResult,
			SpaceTypeDescriptor typeDescriptor) {

		if (qResult.asDocument().contains(typeDescriptor.getIdPropertyName())) {

			Object value = qResult.asDocument()
					.get(typeDescriptor.getIdPropertyName()).getValueAsObject();

			qResult.remove(typeDescriptor.getIdPropertyName());

			qResult.add("_id", value);
		}
	}

	private static StringBuilder parse(String sql) {
		ANTLRInputStream charstream = new ANTLRInputStream(sql);

		SQL2MongoLexer lexer = new SQL2MongoLexer(charstream);

		TokenStream tokenStream = new CommonTokenStream(lexer);

		SQL2MongoParser parser = new SQL2MongoParser(tokenStream);

		SQL2MongoBaseVisitor<ParseContext> visitor = new SQL2MongoBaseVisitor<ParseContext>();

		parser.parse().accept(visitor);

		return new StringBuilder(visitor.getQuery().toString());
	}

	public static DocumentBuilder bind(StringBuilder sb, Object[] parameters,
			SpaceTypeDescriptor spaceTypeDescriptor) {

		StringBuilder sb1 = new StringBuilder(sb.toString());

		SpaceDocumentMapper<Document> mapper = new AsyncSpaceDocumentMapper(
				spaceTypeDescriptor);

		DocumentBuilder query = BuilderFactory
				.start(Json.parse(sb1.toString()));

		if (parameters != null) {
			query = replaceParameters(parameters, mapper, query,new Integer(0));
		}

		return query;
	}

	private static DocumentBuilder replaceParameters(Object[] parameters,
			SpaceDocumentMapper<Document> mapper, DocumentBuilder builder,Integer index) {
		

		DocumentBuilder builder1 = BuilderFactory.start();

		for (Element e : builder.asDocument().getElements()) {

			String field = e.getName();

			Object ph = builder.asDocument().get(field).getValueAsObject();

			if (index >= parameters.length)
				return builder;

			if (ph instanceof String) {

				if (PARAM_PLACEHOLDER.equals(ph)) {
					builder1.add(field, mapper.toObject(parameters[index++]))
							.build();
				}
			} else {
				Document element = (Document) ph;

				if (element.contains($REGEX)) {
					for (Element e1 : element.getElements()) {

						if (!$REGEX.equals(e1.getName()))
							continue;

						if (LIKE.equalsIgnoreCase(e1.getValueAsString())) {
							builder1.add(
									field,
									convertLikeExpression((String) parameters[index++]));
						} else if (RLIKE
								.equalsIgnoreCase(e1.getValueAsString()))
							builder1.add(field, Pattern
									.compile((String) parameters[index++],Pattern.CASE_INSENSITIVE));

					}
				} else {
					DocumentBuilder doc = replaceParameters(parameters, mapper,
							BuilderFactory.start(element),index);

					builder1.add(field, doc);
				}
			}
		}

		return builder1;
	}

	private static Pattern convertLikeExpression(String val) {
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

			return Pattern.compile(val);
		}

		return Pattern.compile("");
	}

}
