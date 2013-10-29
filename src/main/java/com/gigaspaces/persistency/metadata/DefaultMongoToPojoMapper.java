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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gigaspaces.document.SpaceDocument;
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

	private static final String CLASS_NOT_FOUND = "class not found ";
	private static final String _ID = "_id";
	private SpaceTypeDescriptor spaceTypeDescriptor;
	private Class<?> type;

	private static final Log logger = LogFactory.getLog(DefaultMongoToPojoMapper.class);

	//TODO: to replaced with fast reflection ProcedureCache by giga
	private final Map<String, Method> setters = new HashMap<String, Method>();

	public DefaultMongoToPojoMapper(SpaceTypeDescriptor spaceTypeDescriptor) {

		if (spaceTypeDescriptor == null)
			throw new IllegalArgumentException(
					"spaceTypeDescriptior can notnbe null");

		this.spaceTypeDescriptor = spaceTypeDescriptor;

		try {
			if (!spaceTypeDescriptor.supportsDynamicProperties()) {

				this.type = Class.forName(this.spaceTypeDescriptor
						.getTypeName());

				initFields(type, setters);
			} else {
				
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
			Object d = bson.get(key);
			if (d instanceof DBObject) {
				doc.setProperty(key, mapDocument((DBObject) d));
			} else {
				if (_ID.equals(key)) {
					doc.setProperty(spaceTypeDescriptor.getIdPropertyName(), d);
				} else
					doc.setProperty(key, d);
			}

		}

		return doc;
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
			//TODO: shadi throw exception
		} catch (InstantiationException e) {
			e.printStackTrace();
			//TODO: shadi throw exception
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			//TODO: shadi throw exception
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

		if (sp.getType().isEnum() && arg0 != null) {
			arg0 = Enum.valueOf((Class<Enum>) sp.getType(), arg0.toString());
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

		Method idSetter = setters.get(sourcePropertyId);
		Object arg0 = prepareArgument(bson, destinationPropertyId,
				spaceTypeDescriptor.getFixedProperty(sourcePropertyId));

		ReflectionUtils.invokeMethod(idSetter, pojo, new Object[] { arg0 });
	}

	private Object create() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {

		return type.newInstance();
	}
}
