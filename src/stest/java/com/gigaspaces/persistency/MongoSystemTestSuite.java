package com.gigaspaces.persistency;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.gigaspaces.persistency.helper.GSAgentController;
import com.gigaspaces.persistency.helper.MongoDBController;

@RunWith(Suite.class)
@SuiteClasses(value = {
/*
 * BasicMongoSpaceAllInCacheTest.class, BasicMongoSpaceLRUTest.class,
 * 
 * BasicMongoSpacePartialUpdateTest.class
 * 
 * SpaceMongoInitalLoadTest.class
 
SpaceMongoLoadTest.class*/
		
DocumentPojoWithDynamicPropsMongoSpaceTest.class})
public class MongoSystemTestSuite {

	private static final GSAgentController GS_AGENT_CONTROLLER = new GSAgentController();
	private static final MongoDBController MONGO_DB_CONTROLLER = new MongoDBController();

	@BeforeClass
	public static void beforeSuite() {

		MONGO_DB_CONTROLLER.start();

		startGSAgent();
	}

	/**
	 * start gs agent
	 */
	public static void startGSAgent() {
		GS_AGENT_CONTROLLER.start();
	}

	@AfterClass
	public static void afterSuite() {
		stopGSAgent();

		MONGO_DB_CONTROLLER.stop();
	}

	/**
	 * stop GS Agent
	 */
	public static void stopGSAgent() {
		GS_AGENT_CONTROLLER.stop();
	}
}
