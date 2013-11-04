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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gigaspaces.document.DocumentObjectConverter;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.internal.metadata.pojo.PojoPropertyInfo;
import com.gigaspaces.internal.metadata.pojo.PojoTypeInfo;
import com.gigaspaces.internal.metadata.pojo.PojoTypeInfoRepository;
import com.gigaspaces.internal.utils.ReflectionUtils;
import com.gigaspaces.metadata.SpacePropertyDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.error.SpaceMongoDataSourceException;
import com.mongodb.DBObject;

/**
 * @author Shadi Massalha
 * 
 *         Implementation of {@link Mapper} this class map mongoDB types to POJO
 *         one.
 * 
 */
public class DefaultMongoToPojoMapper extends MetadataUtils implements
		Mapper<DBObject, Object> {

	private static final String VALUE = "value";
	private static final String TYPE = "__type";
	private static final String CLASS_NOT_FOUND = "class not found ";
	private static final String _ID = "_id";
	private SpaceTypeDescriptor spaceTypeDescriptor;
	private Class<?> type;

	private static final Log logger = LogFactory
			.getLog(DefaultMongoToPojoMapper.class);

	// TODO: to replaced with fast reflection ProcedureCache by giga
	private Map<String, Method> _setters = new HashMap<String, Method>();
	private final static Map<String, Map<String, Method>> pojoTypeCache = new ConcurrentHashMap<String, Map<String, Method>>();

	public DefaultMongoToPojoMapper(SpaceTypeDescriptor spaceTypeDescriptor) {

		if (spaceTypeDescriptor == null)
			throw new IllegalArgumentException(
					"spaceTypeDescriptior can notnbe null");

		this.spaceTypeDescriptor = spaceTypeDescriptor;

		try {
			if (!spaceTypeDescriptor.supportsDynamicProperties()) {

				this.type = Class.forName(this.spaceTypeDescriptor
						.getTypeName());

				initFields(type, _setters);
			}
		} catch (ClassNotFoundException e) {
			String message = CLASS_NOT_FOUND
					+ spaceTypeDescriptor.getTypeName();
			logger.error(message, e);
			throw new SpaceMongoDataSourceException(message, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gigaspaces.persistency.metadata.Mapper#maps(java.lang.Object)
	 */
	public synchronized Object maps(DBObject bson) {

		if (bson == null)
			return null;

		if (spaceTypeDescriptor.supportsDynamicProperties())
			return mapDocument(bson);

		return mapPojo(bson);
	}

	/**
	 * maps mongoDB {@link DBObject} to {@link SpaceDocument}
	 * 
	 * @param bson
	 *            - mongoDB BSON data structure
	 * @return - {@link SpaceDocument}
	 */
	private Object mapDocument(DBObject bson) {
		SpaceDocument doc = new SpaceDocument(spaceTypeDescriptor.getTypeName());

		for (String key : bson.keySet()) {

			Object data = bson.get(key);

			if (_ID.equals(key)) {
				doc.setProperty(spaceTypeDescriptor.getIdPropertyName(),
						convert(data));
			} else {
				doc.setProperty(key, convert(data));
			}

		}

		return doc;
	}

	private Object convert(Object data) {

		if (data instanceof DBObject) {
			DBObject convertedData = (DBObject) data;

			if (convertedData.containsField(TYPE)) {
				String key = convertedData.get(TYPE).toString();
				Object value = convertedData.get(VALUE);

				if (BigInteger.class.getName().equals(key)) {
					return new BigInteger(value.toString());
				} else if (BigDecimal.class.getName().equals(key)) {
					return new BigDecimal(value.toString());
				} else if (Float.class.getName().equals(key)) {
					return Float.valueOf(value.toString());
				} else if (Byte.class.getName().equals(key)) {
					return Byte.valueOf(value.toString());
				} else if (Character.class.getName().equals(key)) {
					return new Character(value.toString().charAt(0));
				} else {

					return convertDBObjectToPojo(convertedData);
				}

			} else {
				return mapDocument((DBObject) data);
			}
		}

		return data;
	}

	/**
	 * @param json
	 * @return
	 */
	private Object convertDBObjectToPojo(DBObject json) {
		Object pojo = null;

		try {
			Class<?> type = Class.forName(json.get(TYPE).toString());

			Map<String, Method> setters = pojoTypeCache.get(type.getName());

			if (setters == null)
				setters = cachePojo(type);

			pojo = type.newInstance();

			for (String property : setters.keySet()) {
				Object val = json.get(property);

				Method setter = setters.get(property);

				ReflectionUtils.invokeMethod(setter, pojo,
						new Object[] { convert(val) });
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return pojo;
	}

	/**
	 * @param type
	 * @return
	 */
	private Map<String, Method> cachePojo(Class<?> type) {
		PojoTypeInfo typeInfo = PojoTypeInfoRepository.getPojoTypeInfo(type);

		HashMap<String, Method> mapType = new HashMap<String, Method>();

		for (PojoPropertyInfo property : typeInfo.getProperties().values()) {
			if ("class".equals(property.getName())) {
				continue;
			}

			if (property.getGetterMethod() == null) {
				continue;
			}

			mapType.put(property.getName(), property.getSetterMethod());
		}

		pojoTypeCache.put(type.getName(), mapType);

		return mapType;
	}

	private Object mapPojo(DBObject bson) {
		Object pojo = null;

		try {
			if (logger.isTraceEnabled()) {
				logger.trace("DefaultMongoToPojoMapper.mapPojo(" + bson + ")");
			}
			pojo = create();

			mapIdProperty(bson, pojo);

			mapFixedProperties(bson, pojo);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			// TODO: shadi throw exception
		} catch (InstantiationException e) {
			e.printStackTrace();
			// TODO: shadi throw exception
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			// TODO: shadi throw exception
		}

		return pojo;
	}

	private void mapFixedProperties(DBObject bson, Object pojo) {

		for (String key : bson.keySet()) {
			SpacePropertyDescriptor sp = spaceTypeDescriptor
					.getFixedProperty(key);

			if (sp == null
					|| spaceTypeDescriptor.getIdPropertyName().equals(
							sp.getName()))
				continue;

			mapProperty(bson, pojo, sp.getName(), sp.getName());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object prepareArgument(DBObject bson, String key,
			SpacePropertyDescriptor sp) {

		Object arg0 = bson.get(key);

		if (arg0 == null)
			return null;

		if (sp.getType().isEnum()) {
			arg0 = Enum.valueOf((Class<Enum>) sp.getType(), arg0.toString());
		} else if (BigInteger.class.equals(sp.getType())) {
			arg0 = BigInteger.valueOf(Long.parseLong(arg0.toString()));
		} else if (BigDecimal.class.equals(sp.getType())) {
			arg0 = BigDecimal.valueOf(Double.valueOf(arg0.toString()));
		} else if (arg0 instanceof DBObject) {
			arg0 = BSONtoPojo(sp.getType(), (DBObject) arg0);
		}

		return arg0;
	}

	private void mapIdProperty(DBObject bson, Object pojo) {

		String propertyId = spaceTypeDescriptor.getIdPropertyName();

		mapProperty(bson, pojo, propertyId, _ID);
	}

	private void mapProperty(DBObject bson, Object pojo,
			String sourcePropertyId, String destinationPropertyId) {

		Method idSetter = _setters.get(sourcePropertyId);
		Object arg0 = prepareArgument(bson, destinationPropertyId,
				spaceTypeDescriptor.getFixedProperty(sourcePropertyId));

		ReflectionUtils.invokeMethod(idSetter, pojo, new Object[] { arg0 });
	}

	private Object create() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {

		return type.newInstance();
	}
}
