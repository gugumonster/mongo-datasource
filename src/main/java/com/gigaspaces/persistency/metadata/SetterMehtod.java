package com.gigaspaces.persistency.metadata;

import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

public class SetterMehtod implements Setter {

	private Method setter;

	public SetterMehtod(Method setter) {
		if (setter == null)
			throw new IllegalArgumentException("setter method can not be null");

		this.setter = setter;
	}

	public void invokeSetter(Object target, Object arg0) {
		try {
			ReflectionUtils.invokeMethod(setter, target, arg0);
		} catch (IllegalArgumentException ex) {
			System.err.println(ex);
		}

	}
}
