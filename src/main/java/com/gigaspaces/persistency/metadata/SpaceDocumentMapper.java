package com.gigaspaces.persistency.metadata;


public interface SpaceDocumentMapper<T> {

	Object toDocument(T bson);

	T toDBObject(Object document);
	
	Object fromDBObject(Object value);
	
	Object toObject(Object property);
}
