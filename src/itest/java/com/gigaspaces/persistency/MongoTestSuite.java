package com.gigaspaces.persistency;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.gigaspaces.logger.GSLogConfigLoader;
import com.gigaspaces.persistency.helper.EmbeddedMongoController;

@RunWith(Suite.class)
@SuiteClasses(value = {
		BasicMongoTest.class,
		BasicQueriesMongoTest.class,
		DifferentTypesQueryMongoTest.class,
		InitialDataLoadMongoTest.class,
		MetadataSpaceTypeDescriptorConversionTest.class,
		MultiTypeMongoTest.class,
		PojoWithPrimitiveTypesMongoTest.class,
		ReadByIdsMongoTest.class,
		WriteAndRemoveMongoTest.class,
		ReadByIdWithPropertyAddedLaterMongoTest.class,
		DataIteratorWithPropertyAddedLaterMongoTest.class
})
public class MongoTestSuite {

	private static final AtomicInteger runningNumber = new AtomicInteger(0);
	private static volatile boolean isSuiteMode = false;

	private static final EmbeddedMongoController mongoController = new EmbeddedMongoController();

	@BeforeClass
	public static void beforeSuite() throws IOException {
		GSLogConfigLoader.getLoader();
		isSuiteMode = true;
		mongoController.initMongo();
	}

	@AfterClass
	public static void afterSuite() {
		isSuiteMode = false;
		mongoController.stopMongo();
	}

	public static String createDatabaseAndReturnItsName() {

        String dbName = "space" + runningNumber.incrementAndGet();
        mongoController.createDb(dbName);
        return dbName;
	}
	
    public static void dropDb(String dbName)
    {
        mongoController.dropDb(dbName);
    }

	public static boolean isSuiteMode() {
		return isSuiteMode;
	}

	public static int getPort() {

		return mongoController.getPort();
	}

}
