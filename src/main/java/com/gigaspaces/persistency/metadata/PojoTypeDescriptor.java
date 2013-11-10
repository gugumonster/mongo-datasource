package com.gigaspaces.persistency.metadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.gigaspaces.internal.metadata.pojo.PojoPropertyInfo;
import com.gigaspaces.internal.metadata.pojo.PojoTypeInfo;
import com.gigaspaces.internal.metadata.pojo.PojoTypeInfoRepository;
import com.gigaspaces.persistency.error.SpaceMongoException;

public class PojoTypeDescriptor {

	// POJO type
	private Class<?> type;

	private Constructor<Object> constructor;

	private final Map<String, Method> getters = new HashMap<String, Method>();
	private final Map<String, Method> setters = new HashMap<String, Method>();

	@SuppressWarnings("unchecked")
	public PojoTypeDescriptor(Class<?> type) {

		if (type == null)
			throw new IllegalArgumentException("type can not be null");

		PojoTypeInfo typeInfo = PojoTypeInfoRepository.getPojoTypeInfo(type);

		try {
			constructor = (Constructor<Object>) type.getConstructor();
		} catch (SecurityException e) {
			throw new SpaceMongoException(
					"Could not find default constructor for type: "
							+ type.getName(), e);
		} catch (NoSuchMethodException e) {

			throw new SpaceMongoException(
					"Could not find default constructor for type: "
							+ type.getName(), e);
		}

		for (PojoPropertyInfo property : typeInfo.getProperties().values()) {
			if ("class".equals(property.getName())) {
				continue;
			}

			if (property.getGetterMethod() == null
					|| property.getSetterMethod() == null) {
				continue;
			}

			getters.put(property.getName(), property.getGetterMethod());
			setters.put(property.getName(), property.getSetterMethod());
		}

	}

	public Class<?> getType() {
		return type;
	}

	public Constructor<Object> getConstructor() {
		return constructor;
	}

	public Map<String, Method> getGetters() {
		return getters;
	}

	public Map<String, Method> getSetters() {
		return setters;
	}
}
