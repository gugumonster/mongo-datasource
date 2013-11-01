package com.gigaspaces.persistency;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.gigaspaces.logger.GSLogConfigLoader;
import com.gigaspaces.persistency.helper.EmbeddedMongoController;

@RunWith(Suite.class)
@SuiteClasses(value = {

})
public class MongoTestSuite {

	private static volatile boolean isSuiteMode = false;

	private static final EmbeddedMongoController mongoController = new EmbeddedMongoController();

	@BeforeClass
	public static void beforeSuite() {
		GSLogConfigLoader.getLoader();
		isSuiteMode = true;
		mongoController.initMongo(false);
	}

	@AfterClass
	public static void afterSuite() {
		isSuiteMode = false;
		mongoController.stopMongo();
	}

	public static boolean isSuiteMode() {
		return isSuiteMode;
	}

	public static int getPort() {

		return mongoController.getPort();
	}
}
