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

/**
 * @author Shadi Massalha
 * 
 */
public class MongoQueryFactory {

	private static final String $REGEX = "$regex";
	private static final String LIKE = "like()";
	private static final String RLIKE = "rlike()";
	private static final String PARAM_PLACEHOLDER = "'%{}'";
	private static final Map<SpaceTypeDescriptor, Map<String, String>> cachedQuery = new ConcurrentHashMap<SpaceTypeDescriptor, Map<String, String>>();

	public static DocumentBuilder create(DataSourceQuery sql) {
		Map<String, String> cache = cachedQuery.get(sql.getTypeDescriptor());

		if (cache == null) {
			cache = new HashMap<String, String>();

			cachedQuery.put(sql.getTypeDescriptor(), cache);
		}

		String query = sql.getAsSQLQuery().getQuery();

        String parsedQuery = cache.get(query);

		if (parsedQuery == null) {
			parsedQuery = parse(query);

			cache.put(query, parsedQuery);
		}

		DocumentBuilder queryResult = bind(parsedQuery, sql.getAsSQLQuery()
				.getQueryParameters(), sql.getTypeDescriptor());

		replaceIdProperty(queryResult, sql.getTypeDescriptor());

		return queryResult;
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

	private static String parse(String sql) {
		ANTLRInputStream charstream = new ANTLRInputStream(sql);

		SQL2MongoLexer lexer = new SQL2MongoLexer(charstream);

		TokenStream tokenStream = new CommonTokenStream(lexer);

		SQL2MongoParser parser = new SQL2MongoParser(tokenStream);

		SQL2MongoBaseVisitor<ParseContext> visitor = new SQL2MongoBaseVisitor<ParseContext>();

		parser.parse().accept(visitor);

		return visitor.getQuery().toString();
	}

	public static DocumentBuilder bind(String parsedQuery, Object[] parameters,
			SpaceTypeDescriptor spaceTypeDescriptor) {

		SpaceDocumentMapper<Document> mapper = new AsyncSpaceDocumentMapper(spaceTypeDescriptor);

		DocumentBuilder query = BuilderFactory.start(Json.parse(parsedQuery));

		if (parameters != null) {
			query = replaceParameters(parameters, mapper, query, 0);
		}

		return query;
	}

	private static DocumentBuilder replaceParameters(Object[] parameters,
			SpaceDocumentMapper<Document> mapper, DocumentBuilder builder,Integer index) {
		

		DocumentBuilder newBuilder = BuilderFactory.start();

		for (Element e : builder.asDocument().getElements()) {

			String field = e.getName();
			Object ph = e.getValueAsObject();

			if (index >= parameters.length)
				return builder;

			if (ph instanceof String) {

				if (PARAM_PLACEHOLDER.equals(ph)) {
					newBuilder.add(field, mapper.toObject(parameters[index++]))
							.build();
				}
			} else {
				Document element = (Document) ph;

				if (element.contains($REGEX)) {
					for (Element e1 : element.getElements()) {

						if (!$REGEX.equals(e1.getName()))
							continue;

						if (LIKE.equalsIgnoreCase(e1.getValueAsString())) {
							newBuilder.add(
                                    field,
                                    convertLikeExpression((String) parameters[index++]));
						} else if (RLIKE
								.equalsIgnoreCase(e1.getValueAsString()))
							newBuilder.add(field, Pattern
									.compile((String) parameters[index++],Pattern.CASE_INSENSITIVE));

					}
				} else {
					DocumentBuilder doc = replaceParameters(parameters, mapper,
							BuilderFactory.start(element),index);

					newBuilder.add(field, doc);
				}
			}
		}

		return newBuilder;
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
