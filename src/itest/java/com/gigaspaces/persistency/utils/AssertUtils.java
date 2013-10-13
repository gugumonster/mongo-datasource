package com.gigaspaces.persistency.utils;

import java.util.List;

import org.junit.Assert;

public class AssertUtils {

	public static void AssertEquals(String message, List<?> expecteds, List<?> actuals) {

		for(int i=0;i<actuals.size();i++){
			Assert.assertEquals(message,expecteds.get(i), actuals.get(i));			
		}
	}
}
