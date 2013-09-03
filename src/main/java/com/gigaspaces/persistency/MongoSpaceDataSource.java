package com.gigaspaces.persistency;

import java.net.UnknownHostException;

import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.datasource.DataSourceIdQuery;
import com.gigaspaces.datasource.DataSourceIdsQuery;
import com.gigaspaces.datasource.DataSourceQuery;
import com.gigaspaces.datasource.SpaceDataSource;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.error.MongoMetadataException;
import com.gigaspaces.persistency.metadata.MongoMetadataInspector;

public class MongoSpaceDataSource extends SpaceDataSource {

	MongoMetadataInspector inspector = new MongoMetadataInspector();
	private MongoClientPool mongoClientPool;

	public MongoSpaceDataSource(MongoClientPool mongoClientPool) {
		this.mongoClientPool = mongoClientPool;
	}

	@Override
	public DataIterator<SpaceTypeDescriptor> initialMetadataLoad() {
		try {
			inspector.inspectDB("mydb");

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoMetadataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return super.initialMetadataLoad();
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
		// TODO Auto-generated method stub
		return super.initialDataLoad();
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
