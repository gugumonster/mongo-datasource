package com.gigaspaces.persistency.metadata;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

public class SetterField implements Setter {

	private Field field;

	public SetterField(Field field) {

		if (field == null)
			throw new IllegalArgumentException("field can not be null");

		this.field = field;
	}

	public final synchronized void invokeSetter(Object target, Object arg0) {

		boolean access = field.isAccessible();
		try {
			field.setAccessible(true);
			ReflectionUtils.setField(field, target, arg0);
		} finally {
			field.setAccessible(access);
		}
	}

	public synchronized Class<?> getType() {
		return field.getType();
	}
}
