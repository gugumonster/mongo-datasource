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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.internal.metadata.pojo.PojoPropertyInfo;
import com.gigaspaces.internal.metadata.pojo.PojoTypeInfo;
import com.gigaspaces.internal.metadata.pojo.PojoTypeInfoRepository;
import com.gigaspaces.internal.utils.ReflectionUtils;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DefaultPojoToMongoMapper implements
		Mapper<SpaceDocument, DBObject> {

	private SpaceTypeDescriptor spaceTypeDescriptor;
	
	public DefaultPojoToMongoMapper(SpaceTypeDescriptor spaceTypeDescriptor) {
		if (spaceTypeDescriptor == null)
			throw new IllegalArgumentException(
					"spaceTypeDescriptor can not be null");

		this.spaceTypeDescriptor = spaceTypeDescriptor;
	}

	public synchronized DBObject maps(SpaceDocument spaceDocument) {

		DBObject obj = _maps(spaceDocument.getProperties());

		return obj;
	}

	private DBObject _maps(Map<String, Object> properties) {
		Map<String, Object> map2 = new LinkedHashMap<String, Object>();

		for (Entry<String, Object> entry : properties.entrySet()) {

			if (entry.getValue() instanceof SpaceDocument) {
				Map<String, Object> m = ((SpaceDocument) entry.getValue())
						.getProperties();

				map2.put(entry.getKey(), _maps(m));
			} else {

				if (isId(entry.getKey()))
					map2.put("_id", DataConversionUtils.convert(entry.getValue()));
				else {
					map2.put(entry.getKey(), DataConversionUtils.convert(entry.getValue()));
				}
			}
		}

		return new BasicDBObject(map2);
	}

	private boolean isId(String value) {

		return spaceTypeDescriptor.getIdPropertyName().equals(value);
	}

	// private Object convert(Object value) {
	//
	// if (value == null)
	// return null;
	//
	// Class<?> type = value.getClass();
	//
	// if (isPojo(value)) {
	// return convertToDBObject(value, type);
	// } else if (type.isEnum())
	// return value.toString();
	// else if (value instanceof BigInteger) {
	// return createSpetialType(BigInteger.class.getName(),
	// ((BigInteger) value).toString());
	// } else if (value instanceof BigDecimal) {
	// return createSpetialType(BigDecimal.class.getName(),
	// ((BigDecimal) value).toString());
	// } else if (value instanceof Float) {
	// return createSpetialType(Float.class.getName(),
	// ((Float) value).toString());
	// } else if (value instanceof Byte) {
	// return createSpetialType(Byte.class.getName(), value.toString());
	// } else if (value instanceof Character) {
	// return createSpetialType(Character.class.getName(),
	// value.toString());
	// }
	//
	// return value;
	// }

	private boolean isPojo(Object value) {

		return (value instanceof Serializable)
				&& !(value instanceof String || value instanceof Byte
						|| value instanceof Integer || value instanceof Long
						|| value instanceof Short || value instanceof Double
						|| value instanceof Float || value instanceof Character
						|| value instanceof BigDecimal
						|| value instanceof BigInteger
						|| value instanceof Boolean || value instanceof UUID
						|| value instanceof Date || value instanceof byte[]);
	}

//	/**
//	 * @param value
//	 * @param type
//	 * @return
//	 */
//	private Object convertToDBObject(Object value, Class<?> type) {
//		Map<String, Method> getters = pojoTypeCache.get(type.getName());
//
//		if (getters == null)
//			getters = cachePojo(type);
//
//		DBObject pojo = new BasicDBObject("__type", type.getName());
//
//		for (String property : getters.keySet()) {
//
//			Object val = ReflectionUtils.invokeMethod(getters.get(property),
//					value);
//
//			pojo.put(property, myUtils.convert(val));
//		}
//
//		return pojo;
//	}
//
//	/**
//	 * @param type
//	 * @return
//	 */
//	private Map<String, Method> cachePojo(Class<?> type) {
//		PojoTypeInfo typeInfo = PojoTypeInfoRepository.getPojoTypeInfo(type);
//
//		HashMap<String, Method> mapType = new HashMap<String, Method>();
//
//		for (PojoPropertyInfo property : typeInfo.getProperties().values()) {
//			if ("class".equals(property.getName())) {
//				continue;
//			}
//
//			if (property.getGetterMethod() == null) {
//				continue;
//			}
//
//			mapType.put(property.getName(), property.getGetterMethod());
//		}
//
//		pojoTypeCache.put(type.getName(), mapType);
//
//		return mapType;
//	}
//
//	/**
//	 * @param value
//	 * @param m
//	 * @return
//	 */
//	private Object createSpetialType(String type, String value) {
//		Map<String, String> m = new HashMap<String, String>();
//		m.put("__type", type);
//		m.put("value", value);
//
//		return new BasicDBObject(m);
//	}

}
