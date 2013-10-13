package com.gigaspaces.persistency.datasource;

import java.util.Collection;
import java.util.Iterator;

import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.MongoClientPool;
import com.gigaspaces.persistency.metadata.DefaultMongoToPojoMapper;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoInitialDataLoadIterator implements DataIterator<Object> {

	private DBCursor currenCursor;
	private MongoClientPool mongoClientPool;
	private Iterator<SpaceTypeDescriptor> types;
	private SpaceTypeDescriptor spaceTypeDescriptor;
	private DefaultMongoToPojoMapper pojoMapper;

	public MongoInitialDataLoadIterator(Collection<SpaceTypeDescriptor> type,
			MongoClientPool mongoClientPool) {
		if (type == null)
			throw new IllegalArgumentException("");

		this.mongoClientPool = mongoClientPool;
		this.types = type.iterator();
		this.currenCursor = nextDataIterator();

	}

	public boolean hasNext() {

		while (currenCursor != null && !currenCursor.hasNext()) {
			currenCursor = nextDataIterator();
		}
		return currenCursor != null;
	}

	public Object next() {
		DBObject obj = currenCursor.next();

		Object pojo = pojoMapper.maps(obj);

		return pojo;
	}

	public void remove() {
		currenCursor.remove();

	}

	public void close() {
		if (currenCursor != null)
			currenCursor.close();
	}

	private DBCursor nextDataIterator() {

		if (!types.hasNext())
			return null;

		spaceTypeDescriptor = types.next();
		this.pojoMapper = new DefaultMongoToPojoMapper(spaceTypeDescriptor);
		DBCursor cursor = mongoClientPool.getCollection(
				spaceTypeDescriptor.getTypeName()).find();

		return cursor;
	}
}
