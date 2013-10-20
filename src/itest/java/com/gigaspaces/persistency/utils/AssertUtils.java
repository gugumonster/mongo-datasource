package com.gigaspaces.persistency.utils;

import java.util.List;

import org.junit.Assert;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.emory.mathcs.backport.java.util.Collections;

public class AssertUtils {

	public static void assertEquivalent(String message, List<?> expecteds,
			List<?> actuals) {

		for (int i = 0; i < actuals.size(); i++) {
			Assert.assertEquals(message, expecteds.get(i), actuals.get(i));
		}
	}

	public static void assertEquivalenceArrays(String message,
			Object[] expected, Object[] actual) {

		List<?> expected1 = Arrays.asList(expected);
		List<?> actual1 = Arrays.asList(actual);

		Collections.sort(expected1);
		Collections.sort(actual1);
		
		assertEquivalent(message, expected1, actual1);
	}
}
