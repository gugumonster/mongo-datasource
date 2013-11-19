package com.gigaspaces.persistency.metadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openspaces.persistency.cassandra.meta.mapping.node.ProcedureCache;

import com.gigaspaces.internal.reflection.IConstructor;
import com.gigaspaces.internal.reflection.IGetterMethod;
import com.gigaspaces.internal.reflection.ISetterMethod;

/**
 * helper class for fast reflection
 * 
 * @author Shadi Massalha
 * 
 */
public class PojoRepository {

	private static final Map<String, PojoTypeDescriptor> pojoTypeProcedure = new ConcurrentHashMap<String, PojoTypeDescriptor>();

	private static final ProcedureCache procedureCache = new ProcedureCache();

	public PojoTypeDescriptor introcpect(Class<?> type) {
		if (type == null)
			throw new IllegalArgumentException("type can not be null");

		PojoTypeDescriptor descriptor = new PojoTypeDescriptor(type);

		pojoTypeProcedure.put(type.getName(), descriptor);

		return descriptor;
	}

	public PojoTypeDescriptor getProcedureCache(Class<?> key) {
		return pojoTypeProcedure.get(key.getName());
	}

	public IConstructor<Object> getConstructor(Class<?> type) {
		Constructor<Object> constructor = getPojoDescriptor(type)
				.getConstructor();

		return procedureCache.constructorFor(constructor);
	}

	public IGetterMethod<Object> getGetter(Class<?> type, String property) {
		Method getter = getPojoDescriptor(type).getGetters().get(property);

		return procedureCache.getterMethodFor(getter);
	}

	public ISetterMethod<Object> getSetter(Class<?> type, String property) {
		Method setter = getPojoDescriptor(type).getSetters().get(property);

		return procedureCache.setterMethodFor(setter);
	}

	/**
	 * @param type
	 * @return
	 */
	private PojoTypeDescriptor getPojoDescriptor(Class<?> type) {
		PojoTypeDescriptor descriptor = pojoTypeProcedure.get(type.getName());

		if (descriptor == null)
			descriptor = introcpect(type);

		return descriptor;
	}

	public boolean contains(Class<?> type) {
		return pojoTypeProcedure.containsKey(type.getName());
	}

	public Map<String, Method> getGetters(Class<?> type) {
		return getPojoDescriptor(type).getGetters();

	}

	public Map<String, Method> getSetters(Class<?> type) {
		return getPojoDescriptor(type).getSetters();

	}
}
