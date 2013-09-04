package com.gigaspaces.persistency;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.metadata.DefaultPojoToMongoMapper;
import com.gigaspaces.persistency.metadata.Mappper;
import com.gigaspaces.sync.DataSyncOperation;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * @author Shadi Massalha
 * 
 */
public class MongoClientPool {

	private static final Log logger = LogFactory.getLog(MongoClientPool.class);

	private MongoClient client;
	private String dbName;
	private final Object batchSynchLock = new Object();
	private final Map<SpaceTypeDescriptor, Mappper<SpaceDocument, DBObject>> _mappingCache = new HashMap<SpaceTypeDescriptor, Mappper<SpaceDocument, DBObject>>();

	public MongoClientPool(ServerAddress host, String db) {
		this.client = new MongoClient(host);
		this.dbName = db;
	}

	public synchronized DB checkOut() {
		DB db = client.getDB(dbName);
		return db;

	}

	public void performBatch(DataSyncOperation[] dataSyncOperations) {

		final DB db = checkOut();

		synchronized (batchSynchLock) {
			int len = dataSyncOperations.length;

			for (int i = 0; i < len; i++) {
				DataSyncOperation dataSyncOperation = dataSyncOperations[i];

				if (!dataSyncOperation.supportsDataAsDocument())
					continue;

				SpaceDocument spaceDoc = dataSyncOperation.getDataAsDocument();
				SpaceTypeDescriptor spaceTypeDescriptor = dataSyncOperation
						.getTypeDescriptor();

				Mappper<SpaceDocument, DBObject> mapper = getMapper(spaceTypeDescriptor);

				DBObject obj = mapper.maps(spaceDoc);

				DBCollection col = db.getCollection(spaceTypeDescriptor
						.getTypeSimpleName());

				switch (dataSyncOperation.getDataSyncOperationType()) {
				case WRITE:
				case UPDATE:
				case PARTIAL_UPDATE:
					col.save(obj);
					break;
				case REMOVE:
					col.remove(obj);
					break;
				default: {
					throw new IllegalStateException(
							"Unsupported data sync operation type: "
									+ dataSyncOperation
											.getDataSyncOperationType());
				}
				}

			}
		}
	}

	protected Mappper<SpaceDocument, DBObject> getMapper(
			SpaceTypeDescriptor spaceTypeDescriptor) {
		Mappper<SpaceDocument, DBObject> mapper = _mappingCache
				.get(spaceTypeDescriptor);

		if (mapper == null) {
			mapper = new DefaultPojoToMongoMapper(spaceTypeDescriptor);
			
			_mappingCache.put(spaceTypeDescriptor, mapper);
		}
		return mapper;
	}

	// private BasicDBObject convert(Map<String, Object> properties) {
	//
	// Map<String, Object> map2 = new LinkedHashMap<String, Object>();
	//
	// for (Entry<String, Object> entry : properties.entrySet()) {
	//
	// if (entry.getValue().getClass().isEnum()) {
	// map2.put(entry.getKey(), new BasicDBObject(entry.getValue()
	// .getClass().getSimpleName(), entry.getValue()
	// .toString()));
	// } else if (entry.getValue() instanceof SpaceDocument) {
	// Map<String, Object> m = ((SpaceDocument) entry.getValue())
	// .getProperties();
	//
	// map2.put(entry.getKey(), convert(m));
	// } else {
	// map2.put(entry.getKey(), entry.getValue());
	// }
	// }
	//
	// return new BasicDBObject(map2);
	// }
	public void close() {
		// TODO Auto-generated method stub

	}
}
