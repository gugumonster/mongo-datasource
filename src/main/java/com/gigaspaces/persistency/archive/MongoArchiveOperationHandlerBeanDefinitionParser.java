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
package com.gigaspaces.persistency.archive;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Parses "<os-archive:mongo-archive-handler>
 * 
 * @author Shadi Massalha
 * 
 */
public class MongoArchiveOperationHandlerBeanDefinitionParser extends
		AbstractSingleBeanDefinitionParser {

	private static final String GIGA_SPACE = "gigaSpace";
	private static final String HOST = "host";
	private static final String PASSWORD = "password";
	private static final String USER = "user";
	private static final String DB = "db";
	private static final String PORT = "port";
	private static final String URI = "uri";
	private static final String ADDR = "addr";
	private static final String OPTIONS = "options";
	private static final String CREDENTIALS = "credentials";
	private static final String SEEDS = "seeds";

	private static final String GIGA_SPACE_REF = "giga-space";
	private static final String MONGO_DB = DB;
	private static final String MONGO_USER = USER;
	private static final String MONGO_PASSWORD = PASSWORD;
	private static final String MONGO_HOST = HOST;
	private static final String MONGO_PORT = PORT;
	private static final String MONGO_SEEDS_REF = "seeds-ref";
	private static final String MONGO_CREDENTIALS_REF = "credentials-ref";
	private static final String MONGO_OPTIONS_REF = "options-ref";
	private static final String MONGO_ADDR_REF = "addr-ref";
	private static final String MONGO_URI_REF = "uri-ref";

	@Override
	protected Class<MongoArchiveOperationHandler> getBeanClass(Element element) {
		return MongoArchiveOperationHandler.class;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		super.doParse(element, parserContext, builder);

		String gigaSpace = element.getAttribute(GIGA_SPACE_REF);
		if (StringUtils.hasLength(gigaSpace)) {
			builder.addPropertyReference(GIGA_SPACE, gigaSpace);
		}

		String seeds_ref = element.getAttribute(MONGO_SEEDS_REF);
		if (StringUtils.hasLength(seeds_ref)) {
			builder.addPropertyReference(SEEDS, seeds_ref);
		}

		String credentials_ref = element.getAttribute(MONGO_CREDENTIALS_REF);
		if (StringUtils.hasLength(credentials_ref)) {
			builder.addPropertyReference(CREDENTIALS, credentials_ref);
		}

		String options_ref = element.getAttribute(MONGO_OPTIONS_REF);
		if (StringUtils.hasLength(options_ref)) {
			builder.addPropertyReference(OPTIONS, options_ref);
		}

		String addr_ref = element.getAttribute(MONGO_ADDR_REF);
		if (StringUtils.hasLength(addr_ref)) {
			builder.addPropertyReference(ADDR, addr_ref);
		}

		String uri_ref = element.getAttribute(MONGO_URI_REF);
		if (StringUtils.hasLength(uri_ref)) {
			builder.addPropertyReference(URI, uri_ref);
		}

		String db = element.getAttribute(MONGO_DB);
		if (StringUtils.hasLength(db)) {
			builder.addPropertyValue(DB, db);
		}

		String user = element.getAttribute(MONGO_USER);
		if (StringUtils.hasLength(user)) {
			builder.addPropertyValue(USER, user);
		}

		String password = element.getAttribute(MONGO_PASSWORD);
		if (StringUtils.hasLength(password)) {
			builder.addPropertyValue(PASSWORD, password);
		}

		String host = element.getAttribute(MONGO_HOST);
		if (StringUtils.hasLength(host)) {
			builder.addPropertyValue(HOST, host);
		}

		String port = element.getAttribute(MONGO_PORT);
		if (StringUtils.hasLength(host)) {
			builder.addPropertyValue(PORT, port);
		}
	}

}
