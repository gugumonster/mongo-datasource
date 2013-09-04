package com.gigaspaces.persistency.metadata;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DefaultPojoToMongoMapper implements
		Mappper<SpaceDocument, DBObject> {

	private SpaceTypeDescriptor spaceTypeDescriptor;

	public DefaultPojoToMongoMapper(SpaceTypeDescriptor spaceTypeDescriptor) {
		if (spaceTypeDescriptor == null)
			throw new IllegalArgumentException(
					"spaceTypeDescriptor can not be null");

		this.spaceTypeDescriptor = spaceTypeDescriptor;
	}

	public DBObject maps(SpaceDocument spaceDocument) {

		DBObject obj = convert(spaceDocument.getProperties());

		return obj;
	}

	private DBObject convert(Map<String, Object> properties) {
		Map<String, Object> map2 = new LinkedHashMap<String, Object>();

		for (Entry<String, Object> entry : properties.entrySet()) {

			if (entry.getValue().getClass().isEnum()) {
				map2.put(entry.getKey(), new BasicDBObject(entry.getValue()
						.getClass().getSimpleName(), entry.getValue()
						.toString()));
			} else if (entry.getValue() instanceof SpaceDocument) {
				Map<String, Object> m = ((SpaceDocument) entry.getValue())
						.getProperties();

				map2.put(entry.getKey(), convert(m));
			} else {

				if (isId(entry.getKey()))
					map2.put("_id", entry.getValue());
				else
					map2.put(entry.getKey(), entry.getValue());
			}
		}

		return new BasicDBObject(map2);
	}

	private boolean isId(String value) {
		
		return spaceTypeDescriptor.getIdPropertyName().equals(value);
	}

}
