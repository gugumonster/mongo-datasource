package com.gigaspaces.persistency.metadata;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.DBObject;

/**
 * @author Shadi Massalha
 *
 */
public class MetadataUtils {

	private static final Map<String, Map<String, Setter>> cacheType = new HashMap<String, Map<String, Setter>>();
	private static final Object typeSynchLock = new Object();
	private static final Object setterSynchLock = new Object();

	private final DefaultSetterFactory factory = new DefaultSetterFactory();

	public Object BSONtoPojo(Class<?> clazz, DBObject bson) {

		Object target = create(clazz);

		Map<String, Setter> mapper = getClassMapper(clazz);

		for (String key : bson.keySet()) {

			Setter setter = getSetter(clazz, mapper, key);

			Object data = bson.get(key);

			if (setter.getType().isEnum()) {
				setEnum(setter, target, data);
			} else if (data instanceof DBObject) {
				setObject(setter, target, (DBObject) data);
			} else {
				setPrimitive(setter, target, data);
			}

		}

		return target;
	}

	protected void setPrimitive(Setter setter, Object target, Object arg0) {
		setter.invokeSetter(target, arg0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setEnum(Setter setter, Object target, Object arg0) {

		Enum<?> arg1 = Enum.valueOf((Class<Enum>) setter.getType(),
				arg0.toString());

		setter.invokeSetter(target, arg1);
	}

	protected Object setObject(Setter setter, Object target, DBObject bson) {

		return BSONtoPojo(setter.getType(), bson);
	}

	protected Object create(Class<?> clazz) {
		Object pojo = null;
		try {
			pojo = clazz.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return pojo;
	}

	protected Setter getSetter(Class<?> clazz, Map<String, Setter> mapper,
			String key) {
		Setter setter = null;

		synchronized (setterSynchLock) {
			setter = mapper.get(key);

			if (setter == null) {
				setter = factory.create(clazz, key);
				mapper.put(key, setter);
			}
		}

		return setter;
	}

	protected Map<String, Setter> getClassMapper(Class<?> clazz) {
		Map<String, Setter> mapper = null;

		synchronized (typeSynchLock) {

			cacheType.get(clazz.getName());

			if (mapper == null) {
				mapper = new HashMap<String, Setter>();

				cacheType.put(clazz.getName(), mapper);
			}
		}

		return mapper;
	}

}
