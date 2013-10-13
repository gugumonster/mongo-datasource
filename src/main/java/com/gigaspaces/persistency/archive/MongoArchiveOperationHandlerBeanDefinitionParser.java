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

import java.util.List;

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

	private static final String GIGA_SPACE = "giga-space";
	private static final String MONGO_DB = "db";
	private static final String MONGO_USER = "user";
	private static final String MONGO_PASSWORD = "password";
	//private static final String MONGO_ADRESSES = "addresses";

	// private static final String CASSANDRA_CONSISTENCY="write-consistency";

	@Override
	protected Class<MongoArchiveOperationHandler> getBeanClass(Element element) {
		return MongoArchiveOperationHandler.class;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		super.doParse(element, parserContext, builder);

		String gigaSpace = element.getAttribute(GIGA_SPACE);
		if (StringUtils.hasLength(gigaSpace)) {
			builder.addPropertyReference("gigaSpace", gigaSpace);
		}
		// TODO: check this
		List<?> addresses = parserContext.getDelegate().parseListElement(
				element, builder.getRawBeanDefinition());
		builder.addPropertyValue("addresses", addresses);

		String db = element.getAttribute(MONGO_DB);
		if (StringUtils.hasLength(db)) {
			builder.addPropertyValue("db", db);
		}

		String user = element.getAttribute(MONGO_USER);
		if (StringUtils.hasLength(user)) {
			builder.addPropertyValue("user", user);
		}

		String password = element.getAttribute(MONGO_PASSWORD);
		if (StringUtils.hasLength(password)) {
			builder.addPropertyValue("password", password);
		}
	}

}
