package com.gigaspaces.persistency;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.datasource.DataIteratorAdapter;
import com.gigaspaces.datasource.DataSourceIdQuery;
import com.gigaspaces.datasource.DataSourceIdsQuery;
import com.gigaspaces.datasource.DataSourceQuery;
import com.gigaspaces.datasource.SpaceDataSource;
import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorVersionedSerializationUtils;
import com.gigaspaces.persistency.datasource.DefaultMongoDataIterator;

import com.gigaspaces.persistency.datasource.MongoInitialDataLoadIterator;
import com.gigaspaces.persistency.datasource.MongoSqlQueryDataIterator;
import com.gigaspaces.persistency.metadata.DefaultMongoToPojoMapper;
import com.gigaspaces.persistency.metadata.IndexBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

/**
 * @author Shadi Massalha
 * 
 */
public class MongoSpaceDataSource extends SpaceDataSource {

	private static final Log logger = LogFactory
			.getLog(MongoSpaceDataSource.class);

	private MongoClientPool pool;
	private LinkedList<SpaceTypeDescriptor> types;
	private IndexBuilder indexBuilder;

	public MongoSpaceDataSource(MongoClientPool pool) {
		this.pool = pool;
		this.indexBuilder = new IndexBuilder(pool);
	}

	@Override
	public DataIterator<SpaceTypeDescriptor> initialMetadataLoad() {

		logger.trace("MongoSpaceDataSource.initialMetadataLoad()");

		DB db = pool.checkOut();

		DBCollection metadata = db.getCollection("metadata");

		DBCursor m = metadata.find();

		types = new LinkedList<SpaceTypeDescriptor>();

		while (m.hasNext()) {
			DBObject type = m.next();

			Object b = type.get("value");

			try {

				ObjectInput in = new ObjectInputStream(
						new ByteArrayInputStream((byte[]) b));

				Serializable typeDescriptorVersionedSerializableWrapper = IOUtils
						.readObject(in);

				SpaceTypeDescriptor spaceTypeDescriptor = SpaceTypeDescriptorVersionedSerializationUtils
						.fromSerializableForm(typeDescriptorVersionedSerializableWrapper);

				types.add(spaceTypeDescriptor);

				indexBuilder.ensureIndexes(spaceTypeDescriptor);

			} catch (ClassNotFoundException e) {
				logger.error(e);
				e.printStackTrace();
			} catch (IOException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}

		return new DataIteratorAdapter<SpaceTypeDescriptor>(types.iterator());
	}

	@Override
	public Object getById(DataSourceIdQuery idQuery) {

		DBObject q = new BasicDBObject("_id", idQuery.getId());

		DB db = pool.checkOut();

		DBCollection c = db.getCollection(idQuery.getTypeDescriptor()
				.getTypeSimpleName());

		DBObject cursor = c.findOne(q);

		DefaultMongoToPojoMapper mapper = new DefaultMongoToPojoMapper(
				idQuery.getTypeDescriptor());

		return mapper.maps(cursor);
	}

	@Override
	public DataIterator<Object> getDataIterator(DataSourceQuery query) {
		return new MongoSqlQueryDataIterator(pool, query);
	}

	@Override
	public DataIterator<Object> getDataIteratorByIds(DataSourceIdsQuery arg0) {

		QueryBuilder q = QueryBuilder.start();

		for (Object id : arg0.getIds()) {

			q.or(new BasicDBObject("_id", id));
		}

		DBObject q1 = q.get();

		DB db = pool.checkOut();

		DBCollection c = db.getCollection(arg0.getTypeDescriptor()
				.getTypeSimpleName());

		DBCursor cursor = c.find(q1);

		return new DefaultMongoDataIterator(cursor, arg0.getTypeDescriptor());

	}

	@Override
	public DataIterator<Object> initialDataLoad() {
		return new MongoInitialDataLoadIterator(types, pool);
	}

	@Override
	public boolean supportsInheritance() {
		return false;
	}

	public void close() {
		pool.close();
	}
}
