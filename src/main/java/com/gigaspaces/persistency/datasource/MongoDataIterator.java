package com.gigaspaces.persistency.datasource;

import com.gigaspaces.datasource.DataIterator;
import com.mongodb.DBCursor;

public class MongoDataIterator implements DataIterator<Object> {

	DBCursor cursor;

	public MongoDataIterator(DBCursor cursor) {
		if (cursor == null)
			throw new NullPointerException("mongo cursor can not be null");

		this.cursor = cursor;
	}

	public boolean hasNext() {
		return cursor.hasNext();
	}

	public Object next() {
		return cursor.next();
	}

	public void remove() {
		cursor.remove();
	}

	public void close() {
		cursor.close();
	}

}
