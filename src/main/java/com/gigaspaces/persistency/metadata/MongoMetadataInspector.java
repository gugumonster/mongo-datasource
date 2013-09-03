package com.gigaspaces.persistency.metadata;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gigaspaces.datasource.DataIteratorAdapter;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.gigaspaces.persistency.error.MongoMetadataException;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * @author Shadi Massalha
 * 
 */
public class MongoMetadataInspector {

	
	private static final String VALUE = "value";

	private static final String _ID = "_id";

	private static final String GET_COLLECTION_METADATA = "getCollectionMetadata";

	private static final Object GET_COLLECTION_METADATA_FUNCTION = "function(collection){var mr = db.runCommand({'mapreduce' : collection,'map' : function() { for (var key in this) { emit(key, null);}},'reduce' : function(key, stuff) { return null; },'out': collection + '_keys' });  var result=db[mr.result].distinct('_id');  eval('db.'+collection + '_keys.drop()');  return result; }";

	final Map<String, CollectionMetadata> collectionSchema = new HashMap<String, CollectionMetadata>();

	boolean isInitialize;

	String[] EXECLUDE_PROPERTY_PREFIX = new String[] { _ID };

	String[] EXCLUDE_COLLECTION_PREFIX = new String[] { "system." };

	/**
	 * this method ensure existence of getCollectionMetadata function at the
	 * server side if its not exist will create it
	 */
	private void ensureGetCollectionMetadata(DB db) {

		DBCollection sys_js = db.getCollectionFromString("system.js");

		DBObject o = new BasicDBObject(_ID, GET_COLLECTION_METADATA);

		DBObject function = sys_js.findOne(o);

		if (function == null) {

			StringBuilder builder = new StringBuilder("db.system.js.save({");

			builder.append('"');
			builder.append(_ID);
			builder.append('"');
			builder.append(':');

			builder.append('"');
			builder.append(GET_COLLECTION_METADATA);
			builder.append('"');
			builder.append(',');

			builder.append('"');
			builder.append(VALUE);
			builder.append('"');
			builder.append(':');

			builder.append(GET_COLLECTION_METADATA_FUNCTION);

			builder.append("});");

			CommandResult r = db.doEval(builder.toString());

			// WriteResult wr= sys_js.save(createGetCollectionMetadata());
			// System.err.println(wr);
		}

		db.doEval("db.loadServerScripts()");
	}

	private DBObject createGetCollectionMetadata() {

		BasicDBObject obj = new BasicDBObject(_ID, GET_COLLECTION_METADATA);

		BasicDBObject code = new BasicDBObject("$code",
				GET_COLLECTION_METADATA_FUNCTION);

		obj.append(VALUE, code);

		return obj;
	}

	public void inspectDB(String dbName) throws UnknownHostException,
			MongoMetadataException {
		MongoClient mongoClient = new MongoClient("localhost");
		
		DB db = mongoClient.getDB("mydb");
		ensureGetCollectionMetadata(db);

		Set<String> collections = db.getCollectionNames();

		for (String col : collections) {

			if (isExecludedCollection(col))
				continue;

			BasicDBList arr = (BasicDBList) db.eval(String.format("%s('%s')",
					GET_COLLECTION_METADATA, col));

			CollectionMetadata metadata = new CollectionMetadata(col);

			collectionSchema.put(col, metadata);

			for (int i = 0; i < arr.size(); i++) {
				metadata.addField((String) arr.get(i));
			}
		}
	}

	public void buid() {

		for (String key : collectionSchema.keySet()) {
			SpaceTypeDescriptorBuilder sb = new SpaceTypeDescriptorBuilder(key);

			CollectionMetadata c = collectionSchema.get(key);

			for (String f : c.getFields()) {
				sb.addFixedProperty(f, Object.class);
			}

			sb.create();
		}
	}

	private boolean isExecludedCollection(String col) {
		return isExclude(col, EXCLUDE_COLLECTION_PREFIX);
	}

	private boolean isExclude(String col, String[] prefixes) {
		for (String c : prefixes) {
			if (col.startsWith(c))
				return true;
		}
		return false;
	}
}
