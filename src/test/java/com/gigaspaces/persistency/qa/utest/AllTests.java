package com.gigaspaces.persistency.qa.utest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.gigaspaces.persistency.qa.utest.metadata.DocumentSpacePojoTest;
import com.gigaspaces.persistency.qa.utest.metadata.PojoRepositoryTest;
import com.gigaspaces.persistency.qa.utest.parser.SQL2MongoBaseVisitorTest;

@RunWith(Suite.class)
@SuiteClasses({ 
		DocumentSpacePojoTest.class, 
		PojoRepositoryTest.class,
		SQL2MongoBaseVisitorTest.class })
public class AllTests {
}
