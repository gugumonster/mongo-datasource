package com.gigaspaces.persistency.metadata;

public interface Setter {

	Class<?> getType();

	void invokeSetter(Object target, Object arg0);
}
