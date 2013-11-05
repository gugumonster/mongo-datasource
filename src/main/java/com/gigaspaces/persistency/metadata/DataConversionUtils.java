package com.gigaspaces.persistency.metadata;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.gigaspaces.internal.metadata.pojo.PojoPropertyInfo;
import com.gigaspaces.internal.metadata.pojo.PojoTypeInfo;
import com.gigaspaces.internal.metadata.pojo.PojoTypeInfoRepository;
import com.gigaspaces.internal.utils.ReflectionUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DataConversionUtils {

	private static final Map<String, Map<String, Method>> pojoTypeGettersCache = new ConcurrentHashMap<String, Map<String, Method>>();
	private static final Map<String, Map<String, Method>> pojoTypeSettersCache = new ConcurrentHashMap<String, Map<String, Method>>();

	public static Object convert(Object value) {

		if (value == null)
			return null;

		Class<?> type = value.getClass();

		if (isPojo(value) && !type.isArray()) {
			return convertToDBObject(value, type);
		} else if (type.isEnum())
			return value.toString();
		else if (value instanceof BigInteger) {
			return createSpetialType(BigInteger.class.getName(),
					((BigInteger) value).toString());
		} else if (value instanceof BigDecimal) {
			return createSpetialType(BigDecimal.class.getName(),
					((BigDecimal) value).toString());
		} else if (value instanceof Float) {
			return createSpetialType(Float.class.getName(),
					((Float) value).toString());
		} else if (value instanceof Byte) {
			return createSpetialType(Byte.class.getName(), value.toString());
		} else if (value instanceof Character) {
			return createSpetialType(Character.class.getName(),
					value.toString());
		}

		return value;
	}

	public static Object convertToDBObject(Object value, Class<?> type) {
		Map<String, Method> getters = pojoTypeGettersCache.get(type.getName());

		if (getters == null)
			getters = cachePojo(type);

		DBObject pojo = new BasicDBObject("__type", type.getName());

		for (String property : getters.keySet()) {

			Object val = ReflectionUtils.invokeMethod(getters.get(property),
					value);

			pojo.put(property, convert(val));
		}

		return pojo;
	}

	private static Map<String, Method> cachePojo(Class<?> type) {
		PojoTypeInfo typeInfo = PojoTypeInfoRepository.getPojoTypeInfo(type);

		HashMap<String, Method> mapTypeGetters = new HashMap<String, Method>();
		HashMap<String, Method> mapTypeSetters = new HashMap<String, Method>();

		for (PojoPropertyInfo property : typeInfo.getProperties().values()) {
			if ("class".equals(property.getName())) {
				continue;
			}

			if (property.getGetterMethod() == null) {
				continue;
			}

			mapTypeGetters.put(property.getName(), property.getGetterMethod());
			mapTypeSetters.put(property.getName(), property.getSetterMethod());
		}

		pojoTypeGettersCache.put(type.getName(), mapTypeGetters);
		pojoTypeSettersCache.put(type.getName(), mapTypeSetters);

		return mapTypeGetters;
	}

	public static Object createSpetialType(String type, String value) {

		Map<String, String> m = new HashMap<String, String>();
		m.put("__type", type);
		m.put("value", value);

		return new BasicDBObject(m);
	}

	public static boolean isPojo(Object value) {

		return /*
				 * (value instanceof Serializable) &&
				 */!(value instanceof String || value instanceof Byte
				|| value instanceof Integer || value instanceof Long
				|| value instanceof Short || value instanceof Double
				|| value instanceof Float || value instanceof Character
				|| value instanceof BigDecimal || value instanceof BigInteger
				|| value instanceof Boolean || value instanceof UUID
				|| value instanceof Date || value instanceof byte[]);
	}

}
