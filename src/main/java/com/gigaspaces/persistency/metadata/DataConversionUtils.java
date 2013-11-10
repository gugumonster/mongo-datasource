//package com.gigaspaces.persistency.metadata;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.apache.regexp.RE;
//
//import com.gigaspaces.internal.metadata.pojo.PojoPropertyInfo;
//import com.gigaspaces.internal.metadata.pojo.PojoTypeInfo;
//import com.gigaspaces.internal.metadata.pojo.PojoTypeInfoRepository;
//import com.gigaspaces.internal.reflection.IGetterMethod;
//import com.gigaspaces.internal.utils.ReflectionUtils;
//import com.mongodb.BasicDBList;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBObject;
//
///**
// * @author Shadi Massalha
// * 
// *         helper utility class in data conversion from mongoDB driver data type
// *         to SpaceDocument or Pojo
// */
//public class DataConversionUtils {
//
//	// private static final Map<String, Map<String, Method>>
//	// pojoTypeGettersCache = new ConcurrentHashMap<String, Map<String,
//	// Method>>();
//
//	private static final PojoRepository REPOSITORY = new PojoRepository();
//
//	/**
//	 * 
//	 * convert from Object to ObjectDB
//	 * 
//	 * @param value
//	 *            - is pojo or enum or spetial handled java type
//	 * @param defaultPojoToMongoMapper
//	 * @return
//	 */
//	public static Object convert(Object value) {
//
//		if (value == null)
//			return null;
//
//		Class<?> type = value.getClass();
//
//		/*
//		 * if (value instanceof SpaceDocument && (mapper != null &&
//		 * mapper.length > 0)) { return mapper[0].maps((SpaceDocument) value); }
//		 * else if (IsCollection(value, type)) { return
//		 * convertToBasicDBList(value, type, mapper); } else
//		 */if (isPojo(value) && !type.isArray()) {
//			try {
//				return convertToDBObject(value, type);
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (type.isEnum())
//			return value.toString();
//		else if (value instanceof BigInteger) {
//			return createSpetialType(BigInteger.class.getName(),
//					((BigInteger) value).toString());
//		} else if (value instanceof BigDecimal) {
//			return createSpetialType(BigDecimal.class.getName(),
//					((BigDecimal) value).toString());
//		} else if (value instanceof Float) {
//			return createSpetialType(Float.class.getName(),
//					((Float) value).toString());
//		} else if (value instanceof Byte) {
//			return createSpetialType(Byte.class.getName(), value.toString());
//		} else if (value instanceof Character) {
//			return createSpetialType(Character.class.getName(),
//					value.toString());
//		}
//
//		return value;
//	}
//
//	/**
//	 * @param value
//	 * @param type
//	 * @return
//	 */
//	public static boolean IsCollection(Object value, Class<?> type) {
//		return type.isArray() || value instanceof Collection<?>
//				|| value instanceof Map<?, ?>;
//	}
//
//	/**
//	 * convert arrays and list and map to BasicDBList
//	 * 
//	 * @param value
//	 * @param type
//	 * @return
//	 */
//	public static Object convertToBasicDBList(Object value, Class<?> type) {
//		BasicDBList list = new BasicDBList();
//
//		if (value instanceof Map<?, ?>) {
//			for (Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
//				list.add(new BasicDBObject("key", convert(e.getKey())).append(
//						"value", convert(e.getValue())));
//			}
//		} else {
//			Collection<?> collection;
//
//			if (type.isArray()) {
//
//				collection = Arrays.asList((Object[]) value);
//			} else {
//
//				collection = (Collection<?>) value;
//			}
//
//			for (Object o : collection) {
//				list.add(convert(o));
//			}
//
//		}
//		return list;
//	}
//
//	public static Object convertToDBObject(Object value, Class<?> type)
//			throws IllegalArgumentException, IllegalAccessException,
//			InvocationTargetException {
//
//		if (REPOSITORY.contains(type) == false)
//			REPOSITORY.introcpect(type);
//
//		DBObject pojo = new BasicDBObject("__type", type.getName());
//
//		Map<String, Method> getters = REPOSITORY.getGetters(type);
//
//		for (String property : getters.keySet()) {
//			IGetterMethod<Object> getter = REPOSITORY.getGetter(type, property);
//			Object val = getter.get(value);
//			// Object val = ReflectionUtils.invokeMethod(getters.get(property),
//			// value);
//
//			pojo.put(property, convert(val));
//		}
//
//		return pojo;
//	}
//
///*	private static Map<String, Method> cachePojo(Class<?> type) {
//		
//		REPOSITORY.introcpect(type);
//		//
//		// PojoTypeInfo typeInfo = PojoTypeInfoRepository.getPojoTypeInfo(type);
//		//
//		// HashMap<String, Method> mapTypeGetters = new HashMap<String,
//		// Method>();
//		//
//		// for (PojoPropertyInfo property : typeInfo.getProperties().values()) {
//		// if ("class".equals(property.getName())) {
//		// continue;
//		// }
//		//
//		// if (property.getGetterMethod() == null) {
//		// continue;
//		// }
//		//
//		// mapTypeGetters.put(property.getName(), property.getGetterMethod());
//		// }
//		//
//		// pojoTypeGettersCache.put(type.getName(), mapTypeGetters);
//		//
//		return REPOSITORY.getGetters(type);
//	}*/
//
//	public static Object createSpetialType(String type, String value) {
//
//		Map<String, String> m = new HashMap<String, String>();
//		m.put("__type", type);
//		m.put("value", value);
//
//		return new BasicDBObject(m);
//	}
//
//	public static boolean isPojo(Object value) {
//
//		return !(value instanceof String || value instanceof Byte
//				|| value instanceof Integer || value instanceof Long
//				|| value instanceof Short || value instanceof Double
//				|| value instanceof Float || value instanceof Character
//				|| value instanceof BigDecimal || value instanceof BigInteger
//				|| value instanceof Boolean || value instanceof UUID
//				|| value instanceof Date || value instanceof byte[] || value
//				.getClass().isEnum());
//	}
//
//}
