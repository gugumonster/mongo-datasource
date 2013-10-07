package com.gigaspaces.persistency.utils;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;

public class AssertUtils {

	@SuppressWarnings("unchecked")
	public static void AssertEquals(String message, List expecteds, List actuals) {

		//Collections.sort(expecteds);
		//Collections.sort(actuals);
		
		for(int i=0;i<actuals.size();i++){
			
			Assert.assertTrue(message,expecteds.contains(actuals.get(i)));
		}
	}
}
