package com.gigaspaces.persistency.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

import com.gigaspaces.metadata.SpacePropertyDescriptor;

public class DefaultSetterFactory {

	public Setter create(Class<?> clazz,
			SpacePropertyDescriptor spacePropertyDescriptor) {

		Method method = getSetterMethod(clazz, spacePropertyDescriptor);

		if (method == null) {
			Field field = ReflectionUtils.findField(clazz,
					spacePropertyDescriptor.getName(),
					spacePropertyDescriptor.getType());

			return new SetterField(field);
		}

		return new SetterMehtod(method);
	}

	private Method getSetterMethod(Class<?> clazz,
			SpacePropertyDescriptor spacePropertyDescriptor) {

		String propertyId = spacePropertyDescriptor.getName();

		String setterName = "set" + propertyId.substring(0, 1).toUpperCase()
				+ propertyId.substring(1);

		Method method = ReflectionUtils.findMethod(clazz, setterName,
				spacePropertyDescriptor.getType());

		return method;
	}
}
