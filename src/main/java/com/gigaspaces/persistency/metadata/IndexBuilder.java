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
package com.gigaspaces.persistency.metadata;

import java.util.Map;

import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.builder.Index;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.index.SpaceIndex;
import com.gigaspaces.metadata.index.SpaceIndexType;
import com.gigaspaces.persistency.MongoClientConnector;
import com.gigaspaces.sync.AddIndexData;

public class IndexBuilder {

	private MongoClientConnector client;

	public IndexBuilder(MongoClientConnector pool) {

		this.client = pool;
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

			createIndex(spaceTypeDescriptor.getTypeName(), idx);
		}

		if (!id.equals(routing)) {
			createIndex(spaceTypeDescriptor.getTypeName(), routing,
					SpaceIndexType.BASIC, BuilderFactory.start());
		}
	}

	private void createIndex(String typeSimpleName, String routing,
			SpaceIndexType type, DocumentBuilder option) {

		MongoCollection c = client.getCollection(typeSimpleName);

		Element key = null;

		if (type == SpaceIndexType.BASIC)
			key = Index.hashed(routing);
		else
			key = Index.asc(routing);

		c.createIndex(routing, option.asDocument(), key);
	}

	private void createIndex(String collectionName, SpaceIndex idx) {

		DocumentBuilder option = getOptions(idx);

		createIndex(collectionName, idx.getName(), idx.getIndexType(), option);
	}

	private DocumentBuilder getOptions(SpaceIndex idx) {

		// DBObject option = new BasicDBObject();

		// if (idx.isUnique())
		// option.put("unique", idx.isUnique());

		return BuilderFactory.start();
	}

	public void ensureIndexes(AddIndexData addIndexData) {
		for (SpaceIndex idx : addIndexData.getIndexes()) {

			if (idx.getIndexType() == SpaceIndexType.NONE)
				continue;

			createIndex(addIndexData.getTypeName(), idx);
		}
	}
}
