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

	public final void invokeSetter(Object target, Object arg0) {

		ReflectionUtils.setField(field, target, arg0);

	}

}
