package com.gigaspaces.persistency.datasource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.datasource.DataSourceQuery;
import com.gigaspaces.persistency.MongoClientPool;
import com.gigaspaces.persistency.error.UnSupportedQueryException;
import com.gigaspaces.persistency.metadata.DefaultMongoToPojoMapper;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Shadi Massalha
 * 
 */
public class MongoSqlQueryDataIterator implements DataIterator<Object> {

	private static final Log logger = LogFactory
			.getLog(MongoSqlQueryDataIterator.class);

	private MongoClientPool pool;
	private DataSourceQuery query;
	private DBCursor cursor;
	private DefaultMongoToPojoMapper pojoMapper;

	public MongoSqlQueryDataIterator(MongoClientPool pool, DataSourceQuery query) {
		if (pool == null)
			throw new IllegalArgumentException("");

		if (query == null)
			throw new IllegalArgumentException("query can not be null");

		if (!query.supportsAsSQLQuery())
			throw new UnSupportedQueryException("not sql query");

		this.pool = pool;
		this.query = query;
		this.pojoMapper = new DefaultMongoToPojoMapper(
				query.getTypeDescriptor());
	}

	public boolean hasNext() {
		if (cursor == null) {
			init();
		}

		return cursor.hasNext();
	}

	public Object next() {

		Object result = pojoMapper.maps(cursor.next());

		return result;
	}

	private void init() {
		DBCollection collection = pool.getCollection(query.getTypeDescriptor()
				.getTypeName());

		DBObject q = null;

		logger.debug(query);

		if (query.supportsAsSQLQuery())
			q = MongoQueryFactory.create(query);

		cursor = collection.find(q);

	}

	public void remove() {
		if (cursor != null)
			cursor.remove();
	}

	public void close() {
		if (cursor != null)
			cursor.close();

	}

}
