package com.gigaspaces.persistency.datasource;

import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.metadata.DefaultMongoToPojoMapper;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoDataIterator implements DataIterator<Object> {

	private DBCursor cursor;
	private DefaultMongoToPojoMapper mapper;

	public MongoDataIterator(DBCursor cursor,SpaceTypeDescriptor spaceTypeDescriptor) {
		if (cursor == null)
			throw new NullPointerException("mongo cursor can not be null");

		if (spaceTypeDescriptor == null)
			throw new NullPointerException("spaceTypeDescriptor can not be null");
		
		this.cursor = cursor;
		this.mapper = new DefaultMongoToPojoMapper(spaceTypeDescriptor);		
	}

	public synchronized boolean hasNext() {
		return cursor.hasNext();
	}

	public synchronized Object next() {
		DBObject bson= cursor.next();
		
		return mapper.maps(bson);
	}

	public synchronized void remove() {
		cursor.remove();
	}

	public synchronized void close() {
		cursor.close();
	}

}
