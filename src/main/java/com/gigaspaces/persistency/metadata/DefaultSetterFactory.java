package com.gigaspaces.persistency.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

import com.gigaspaces.metadata.SpacePropertyDescriptor;

public class DefaultSetterFactory {

	public Setter create(Class<?> clazz,
			SpacePropertyDescriptor spacePropertyDescriptor) {

		return create(clazz, spacePropertyDescriptor.getName());
	}

	private Method getSetterMethod(Class<?> clazz, String key) {

		String setterName = "set" + key.substring(0, 1).toUpperCase()
				+ key.substring(1);

		Method method = ReflectionUtils.findMethod(clazz, setterName);

		return method;
	}

	public Setter create(Class<?> clazz, String key) {

		Method method = getSetterMethod(clazz, key);

		if (method == null) {
			Field field = ReflectionUtils.findField(clazz, key);
			
			return new SetterField(field);
		}

		return new SetterMehtod(method);

	}
}
