package com.gigaspaces.persistency.metadata;

import java.util.Map;

import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.index.AbstractSpaceIndex;
import com.gigaspaces.metadata.index.SpaceIndex;
import com.gigaspaces.metadata.index.SpaceIndexType;
import com.gigaspaces.persistency.MongoClientPool;
import com.gigaspaces.sync.AddIndexData;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class IndexBuilder {

	private MongoClientPool pool;

	public IndexBuilder(MongoClientPool pool) {
		this.pool = pool;
	}

	public void ensureIndexes(SpaceTypeDescriptor spaceTypeDescriptor) {

		Map<String, SpaceIndex> indexes = spaceTypeDescriptor.getIndexes();

		String id = spaceTypeDescriptor.getIdPropertyName();
		String routing = spaceTypeDescriptor.getRoutingPropertyName();

		for (String key : indexes.keySet()) {
			SpaceIndex idx = indexes.get(key);

			if (idx.getIndexType() == SpaceIndexType.NONE
					|| idx.getName().equals(id)
					|| idx.getName().equals(routing))
				continue;

			createIndex(spaceTypeDescriptor.getTypeSimpleName(), idx);
		}

		if (!id.equals(routing)) {
			createIndex(spaceTypeDescriptor.getTypeSimpleName(), routing,
					SpaceIndexType.BASIC, new BasicDBObject());
		}
	}

	private void createIndex(String typeSimpleName, String routing,
			SpaceIndexType type, DBObject option) {

		DBCollection c = pool.getCollection(typeSimpleName);

		DBObject keys;

		if (type == SpaceIndexType.BASIC)
			keys = new BasicDBObject(routing, "hashed");
		else
			keys = new BasicDBObject(routing, 1);

		c.ensureIndex(keys, option);
	}

	private void createIndex(String collectionName, SpaceIndex idx) {

		DBObject option = getOptions(idx);

		createIndex(collectionName, idx.getName(), idx.getIndexType(), option);
	}

	private DBObject getOptions(SpaceIndex idx) {
		AbstractSpaceIndex a = (AbstractSpaceIndex) idx;

		DBObject option = new BasicDBObject();

		if (a != null) {

			if (a.isUnique())
				option.put("unique", a.isUnique());
		}
		return option;
	}

	public void ensureIndexes(AddIndexData addIndexData) {
		for (SpaceIndex idx : addIndexData.getIndexes()) {

			if (idx.getIndexType() == SpaceIndexType.NONE)
				continue;

			createIndex(addIndexData.getTypeName(), idx);
		}
	}
}
