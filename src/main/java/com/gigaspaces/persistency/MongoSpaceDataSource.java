package com.gigaspaces.persistency;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;

import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.datasource.DataIteratorAdapter;
import com.gigaspaces.datasource.DataSourceIdQuery;
import com.gigaspaces.datasource.DataSourceIdsQuery;
import com.gigaspaces.datasource.DataSourceQuery;
import com.gigaspaces.datasource.SpaceDataSource;
import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorVersionedSerializationUtils;
import com.gigaspaces.persistency.datasource.MongoInitialDataLoadIterator;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoSpaceDataSource extends SpaceDataSource {

	private MongoClientPool mongoClientPool;
	private LinkedList<SpaceTypeDescriptor> types;

	public MongoSpaceDataSource(MongoClientPool mongoClientPool) {
		this.mongoClientPool = mongoClientPool;
	}

	@Override
	public DataIterator<SpaceTypeDescriptor> initialMetadataLoad() {

		DB db = mongoClientPool.checkOut();

		DBCollection metadata = db.getCollection("metadata");

		DBCursor m = metadata.find();

		types = new LinkedList<SpaceTypeDescriptor>();

		while (m.hasNext()) {
			DBObject type = m.next();

			Object b = type.get("value");

			try {

				ObjectInput in = new ObjectInputStream(
						new ByteArrayInputStream((byte[]) b));

				Serializable typeDescriptorVersionedSerializableWrapper = IOUtils
						.readObject(in);

				SpaceTypeDescriptor spaceTypeDescriptor = SpaceTypeDescriptorVersionedSerializationUtils
						.fromSerializableForm(typeDescriptorVersionedSerializableWrapper);

				types.add(spaceTypeDescriptor);

				System.out.println(spaceTypeDescriptor);

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return new DataIteratorAdapter<SpaceTypeDescriptor>(types.iterator());
	}

	@Override
	public Object getById(DataSourceIdQuery idQuery) {
		// TODO Auto-generated method stub
		return super.getById(idQuery);
	}

	@Override
	public DataIterator<Object> getDataIterator(DataSourceQuery query) {
		// TODO Auto-generated method stub
		return super.getDataIterator(query);
	}

	@Override
	public DataIterator<Object> getDataIteratorByIds(DataSourceIdsQuery arg0) {
		// TODO Auto-generated method stub
		return super.getDataIteratorByIds(arg0);
	}

	@Override
	public DataIterator<Object> initialDataLoad() {
		DataIterator<Object> iterator = super.initialDataLoad();

		try {
			iterator = new MongoInitialDataLoadIterator(types, mongoClientPool);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return iterator;
	}

	@Override
	public boolean supportsInheritance() {
		// TODO Auto-generated method stub
		return super.supportsInheritance();
	}

	public void close() {
		// TODO Auto-generated method stub

	}
}
