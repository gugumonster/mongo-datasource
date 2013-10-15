/*******************************************************************************
 * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.gigaspaces.persistency.metadata;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.gigaspaces.internal.metadata.pojo.PojoPropertyInfo;
import com.gigaspaces.internal.metadata.pojo.PojoTypeInfo;
import com.gigaspaces.internal.metadata.pojo.PojoTypeInfoRepository;
import com.gigaspaces.internal.utils.ReflectionUtils;
import com.mongodb.DBObject;

/**
 * @author Shadi Massalha
 * 
 */
public class MetadataUtils {

	private static final Map<String, Map<String, Method>> cacheType = new HashMap<String, Map<String, Method>>();
	private static final Object typeSynchLock = new Object();
	private static final Object setterSynchLock = new Object();

	public Object BSONtoPojo(Class<?> clazz, DBObject bson) {

		Object target = create(clazz);

		Map<String, Method> mapper = getClassMapper(clazz);

		for (String key : bson.keySet()) {

			Method setter = getSetter(mapper, key);

			Object data = bson.get(key);

			Class<?> type = getSetterType(setter);

			if (type.isEnum()) {
				setEnum(setter, type, target, data);
			} else if (data instanceof DBObject) {
				setObject(type, (DBObject) data);
			} else {
				setPrimitive(setter, target, data);
			}
		}

		return target;
	}

	private Class<?> getSetterType(Method setter) {
		return setter.getParameterTypes()[0];
	}

	protected void setPrimitive(Method setter, Object target, Object arg0) {
		ReflectionUtils.invokeMethod(setter, target, new Object[] { arg0 });
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setEnum(Method setter, Class<?> type, Object target,
			Object arg0) {

		Enum<?> arg1 = Enum.valueOf((Class<Enum>) type, arg0.toString());

		ReflectionUtils.invokeMethod(setter, target, new Object[] { arg1 });

	}

	protected Object setObject(Class<?> type, DBObject bson) {

		return BSONtoPojo(type, bson);
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

	protected Method getSetter(Map<String, Method> setters, String key) {

		Method setter = null;

		synchronized (setterSynchLock) {
			setter = setters.get(key);
		}

		return setter;
	}

	protected Map<String, Method> getClassMapper(Class<?> clazz) {
		Map<String, Method> setters = null;

		synchronized (typeSynchLock) {
			setters = cacheType.get(clazz.getName());

			if (setters == null) {

				setters = new HashMap<String, Method>();

				initFields(clazz, setters);
			}
		}

		return setters;
	}

	protected void initFields(Class<?> type, Map<String, Method> mapper) {
		PojoTypeInfo typeInfo = PojoTypeInfoRepository.getPojoTypeInfo(type);

		for (PojoPropertyInfo property : typeInfo.getProperties().values()) {
			if ("class".equals(property.getName())) {
				continue;
			}

			if (property.getGetterMethod() == null) {
				continue;
			}

			mapper.put(property.getName(), property.getSetterMethod());
		}
	}

}
