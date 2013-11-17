///*******************************************************************************
// * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *******************************************************************************/
//package com.gigaspaces.persistency.metadata;
//
//import java.util.Map;
//
//import com.gigaspaces.metadata.SpaceTypeDescriptor;
//import com.gigaspaces.metadata.index.SpaceIndex;
//import com.gigaspaces.metadata.index.SpaceIndexType;
//import com.gigaspaces.persistency.MongoClientWrapper;
//import com.gigaspaces.sync.AddIndexData;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBCollection;
//import com.mongodb.DBObject;
//
//public class IndexBuilder {
//
//	private MongoClientWrapper client;
//
//	public IndexBuilder(MongoClientWrapper pool) {
//		this.client = pool;
//	}
//
//	// public IndexBuilder(MongoClientWrapperV1 mongoClientWrapperV1) {
//	// // TODO Auto-generated constructor stub
//	// }
//
//	public void ensureIndexes(SpaceTypeDescriptor spaceTypeDescriptor) {
//
//		Map<String, SpaceIndex> indexes = spaceTypeDescriptor.getIndexes();
//
//		String id = spaceTypeDescriptor.getIdPropertyName();
//		String routing = spaceTypeDescriptor.getRoutingPropertyName();
//
//		for (String key : indexes.keySet()) {
//			SpaceIndex idx = indexes.get(key);
//
//			if (idx.getIndexType() == SpaceIndexType.NONE
//					|| idx.getName().equals(id)
//					|| idx.getName().equals(routing))
//				continue;
//
//			createIndex(spaceTypeDescriptor.getTypeName(), idx);
//		}
//
//		if (!id.equals(routing)) {
//			createIndex(spaceTypeDescriptor.getTypeName(), routing,
//					SpaceIndexType.BASIC, new BasicDBObject());
//		}
//	}
//
//	private void createIndex(String typeSimpleName, String routing,
//			SpaceIndexType type, DBObject option) {
//
//		DBCollection c = client.getCollection(typeSimpleName);
//
//		DBObject keys;
//
//		if (type == SpaceIndexType.BASIC)
//			keys = new BasicDBObject(routing, "hashed");
//		else
//			keys = new BasicDBObject(routing, 1);
//
//		c.ensureIndex(keys, option);
//	}
//
//	private void createIndex(String collectionName, SpaceIndex idx) {
//
//		DBObject option = getOptions(idx);
//
//		createIndex(collectionName, idx.getName(), idx.getIndexType(), option);
//	}
//
//	private DBObject getOptions(SpaceIndex idx) {
//
//		DBObject option = new BasicDBObject();
//
//		// if (idx.isUnique())
//		// option.put("unique", idx.isUnique());
//
//		return option;
//	}
//
//	public void ensureIndexes(AddIndexData addIndexData) {
//		for (SpaceIndex idx : addIndexData.getIndexes()) {
//
//			if (idx.getIndexType() == SpaceIndexType.NONE)
//				continue;
//
//			createIndex(addIndexData.getTypeName(), idx);
//		}
//	}
//}
