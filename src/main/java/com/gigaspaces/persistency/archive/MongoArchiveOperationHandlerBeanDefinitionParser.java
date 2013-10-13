package com.gigaspaces.persistency.archive;

import org.jini.rio.qos.measurable.cpu.MpstatOutputParser;
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
	private static final String MONGO_ADRESSES = "addresses";

	// private static final String CASSANDRA_KEYSPACE="keyspace";
	// private static final String CASSANDRA_HOSTS="hosts";
	// private static final String CASSANDRA_PORT="port";
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

		String adresses = element.getAttribute(MONGO_ADRESSES);
		element.getElementsByTagName(MONGO_ADRESSES);
//		if (StringUtils.hasLength(keyspace)) {
//			builder.addPropertyValue("keyspace", keyspace);
//		}

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
