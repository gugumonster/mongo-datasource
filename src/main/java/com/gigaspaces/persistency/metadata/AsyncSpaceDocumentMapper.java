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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.builder.ArrayBuilder;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.ArrayElement;
import com.allanbank.mongodb.bson.element.ObjectId;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.internal.reflection.ISetterMethod;
import com.gigaspaces.metadata.SpaceDocumentSupport;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.persistency.error.SpaceMongoException;

/**
 * class helper to map from mongo document type to SpaceDocument and vice versa
 * 
 * @author Shadi Massalha
 * 
 */
public class AsyncSpaceDocumentMapper implements SpaceDocumentMapper<Document> {

	private static final String _ID = "_id";
	private static final String TYPE = "__type__";
	private static final String VALUE = "__value__";
	private static final byte TYPE_CHAR = Byte.MIN_VALUE;
	private static final byte TYPE_BYTE = Byte.MIN_VALUE + 1;
	private static final byte TYPE_STRING = Byte.MIN_VALUE + 2;
	private static final byte TYPE_BOOLEAN = Byte.MIN_VALUE + 3;
	private static final byte TYPE_INT = Byte.MIN_VALUE + 4;
	private static final byte TYPE_LONG = Byte.MIN_VALUE + 5;
	private static final byte TYPE_FLOAT = Byte.MIN_VALUE + 6;
	private static final byte TYPE_DOUBLE = Byte.MIN_VALUE + 7;
	private static final byte TYPE_SHORT = Byte.MIN_VALUE + 8;
	private static final byte TYPE_UUID = Byte.MIN_VALUE + 9;
	private static final byte TYPE_DATE = Byte.MIN_VALUE + 10;
	private static final byte TYPE_BIGINT = Byte.MIN_VALUE + 11;
	private static final byte TYPE_BIGDECIMAL = Byte.MIN_VALUE + 12;
	private static final byte TYPE_BYTEARRAY = Byte.MIN_VALUE + 13;
	private static final byte TYPE_ARRAY = Byte.MAX_VALUE - 1;
	private static final byte TYPE_COLLECTION = Byte.MAX_VALUE - 2;
	private static final byte TYPE_MAP = Byte.MAX_VALUE - 3;
	private static final byte TYPE_ENUM = Byte.MAX_VALUE - 4;
	private static final byte TYPE_OBJECTID = Byte.MAX_VALUE - 5;
	private static final byte TYPE_OBJECT = Byte.MAX_VALUE;

	private static final Map<Class<?>, Byte> typeCodes = new HashMap<Class<?>, Byte>();

	static {
		typeCodes.put(Boolean.class, TYPE_BOOLEAN);
		typeCodes.put(Byte.class, TYPE_BYTE);
		typeCodes.put(Character.class, TYPE_CHAR);
		typeCodes.put(Short.class, TYPE_SHORT);
		typeCodes.put(short.class, TYPE_SHORT);
		typeCodes.put(Integer.class, TYPE_INT);
		typeCodes.put(Long.class, TYPE_LONG);
		typeCodes.put(Float.class, TYPE_FLOAT);
		typeCodes.put(Double.class, TYPE_DOUBLE);
		typeCodes.put(String.class, TYPE_STRING);
		typeCodes.put(UUID.class, TYPE_UUID);
		typeCodes.put(Date.class, TYPE_DATE);
		typeCodes.put(BigInteger.class, TYPE_BIGINT);
		typeCodes.put(BigDecimal.class, TYPE_BIGDECIMAL);
		typeCodes.put(byte[].class, TYPE_BYTEARRAY);
		typeCodes.put(ObjectId.class, TYPE_OBJECTID);

	}

	private final PojoRepository repository = new PojoRepository();
	private final SpaceTypeDescriptor spaceTypeDescriptor;

	public AsyncSpaceDocumentMapper(SpaceTypeDescriptor spaceTypeDescriptor) {
		this.spaceTypeDescriptor = spaceTypeDescriptor;
	}

	private byte type(Object value) {
		Byte type = typeCodes.get((value instanceof Class<?>) ? value : value
				.getClass());
		if (type == null) {
			if (value.getClass().isEnum())
				type = TYPE_ENUM;
			else if (value.getClass().isArray())
				type = TYPE_ARRAY;
			else if (Collection.class.isInstance(value))
				type = TYPE_COLLECTION;
			else if (Map.class.isInstance(value))
				type = TYPE_MAP;
			else
				type = TYPE_OBJECT;
		}
		return type;
	}

	private byte bsonType(Object value) {

		Byte type = typeCodes.get(value.getClass());

		if (type == null) {

			if (ArrayElement.class.isInstance(value))
				type = TYPE_ARRAY;
			else
				type = TYPE_OBJECT;
		}

		return type;
	}

	public Object toDocument(Document bson) {

		if (bson == null)
			return null;

		String type = bson.get(TYPE).getValueAsString();

		if (isDocument(type))
			return toSpaceDocument(bson);

		return toPojo(bson);
	}

	private Object toPojo(Document bson) {

		String className = bson.get(TYPE).getValueAsString();

		try {
			Class<?> type = getClassFor(className);
            Object pojo = repository.getConstructor(getClassFor(className))
					.newInstance();

			for (Element element : bson.getElements()) {

				String property = element.getName();

				if (TYPE.equals(property))
					continue;

				Object value = bson.get(property);

				boolean isArray = false;
				if (!(value instanceof ArrayElement)) {
					value = ((Element) value).getValueAsObject();
				} else {
					isArray = true;
				}
				if (value == null)
					continue;

				if (_ID.equals(property))
					property = spaceTypeDescriptor.getIdPropertyName();

				ISetterMethod<Object> setter = repository.getSetter(type,
						property);

                Object val;
				if (isArray) {
					val = toExtractArray((ArrayElement) value,
							setter.getParameterTypes()[0]);
				} else {
					val = fromDBObject(value);
                }

				if (type(setter.getParameterTypes()[0]) == TYPE_SHORT)
					val = Short.valueOf(val.toString()).shortValue();

				setter.set(pojo, val);
			}
            return pojo;
		} catch (InvocationTargetException e) {
			throw new SpaceMongoException(
					"can not invoke constructor or method: " + bson, e);
		} catch (InstantiationException e) {
			throw new SpaceMongoException(
					"Could not find default constructor for: " + bson, e);
		} catch (IllegalAccessException e) {
			throw new SpaceMongoException(
					"can not access constructor or method: " + bson, e);
		}
	}

	private Object toSpaceDocument(Document bson) {

		SpaceDocument document = new SpaceDocument(bson.get(TYPE)
				.getValueAsString());

		for (Element element : bson.getElements()) {

			String property = element.getName();

			if (TYPE.equals(property))
				continue;

			Object value = bson.get(property).getValueAsObject();

			if (value == null)
				continue;

			if (_ID.equals(property))
				property = spaceTypeDescriptor.getIdPropertyName();

			document.setProperty(property, fromDBObject(value));
		}

		return document;
	}

	private boolean isDocument(String className) {
		try {
			Class.forName(className);
			return false;
		} catch (ClassNotFoundException e) {
		}

		return true;
	}

	public Object fromDBObject(Object value) {

		if (value == null)
			return null;

		switch (bsonType(value)) {
		case TYPE_OBJECTID:
			return null;
		case TYPE_ARRAY:
			return toExactArray((ArrayElement) value);
		case TYPE_OBJECT:
			return toExactObject(value);
		default:
			return value;
		}
	}

	@SuppressWarnings("unchecked")
	private Object toExactObject(Object value) {
		Document bson = (Document) value;

		if (bson.contains(TYPE) && bson.contains(VALUE)) {
			try {
				@SuppressWarnings("rawtypes")
				Class type = Class.forName(bson.get(TYPE).getValueAsString());

				if (type.isEnum())
					return Enum.valueOf(type, bson.get(VALUE)
							.getValueAsString());
				else
					return fromSpetialType((Document) value);

			} catch (ClassNotFoundException e) {
			}
		}

		return toDocument(bson);
	}

	private Object toExactArray(ArrayElement value) {

		if (value.getEntries().size() < 1)
			throw new IllegalStateException("Illegal BSON array size: "
					+ value.getEntries().size() + ", size must be at lest 1");

		Class<?> type = getClassFor(value.getEntries().get(0)
				.getValueAsString());

		return toExtractArray(value, type);
	}

	private Object toExtractArray(ArrayElement value, Class<?> type) {
		if (type.isArray()) {
			return toArray(type, value);
		} else if (Collection.class.isAssignableFrom(type)) {
			return toCollection(type, value);
		} else if (Map.class.isAssignableFrom(type)) {
			return toMap(type, value);
		}

		throw new SpaceMongoException("invalid Array/Collection/Map type: "
				+ type.getName());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map toMap(Class<?> type, ArrayElement value) {

		try {
            Map map;

			if (!type.isInterface()) {
				map = (Map) repository.getConstructor(type).newInstance();
			} else {
				map = (Map) repository.getConstructor(
						getClassFor(value.getEntries().get(0)
								.getValueAsString())).newInstance();
			}
			for (int i = 1; i < value.getEntries().size(); i += 2) {
				Object key = fromDBObject(value.getEntries().get(i)
						.getValueAsObject());
				Object val = fromDBObject(value.getEntries().get(i + 1)
						.getValueAsObject());

				map.put(key, val);
			}

			return map;

		} catch (InvocationTargetException e) {
			throw new SpaceMongoException("Could not find default constructor for type: " + type.getName(), e);
		} catch (InstantiationException e) {
			throw new SpaceMongoException("Could not find default constructor for type: " + type.getName(), e);
		} catch (IllegalAccessException e) {
            throw new SpaceMongoException("Could not find default constructor for type: " + type.getName(), e);
		}
	}

	private Object toArray(Class<?> type, ArrayElement value) {

		int length = value.getEntries().size() - 1;
		Object array = Array.newInstance(type.getComponentType(), length);

		for (int i = 1; i < length + 1; i++) {
			Object v = fromDBObject(value.getEntries().get(i)
					.getValueAsObject());

			if (SpaceDocument.class.isAssignableFrom(type.getComponentType()))
				v = MongoDocumentObjectConverter.instance().toDocumentIfNeeded(
						v, SpaceDocumentSupport.CONVERT);

			if (type(type.getComponentType()) == TYPE_SHORT)
				v = Short.valueOf(v.toString()).shortValue();

			Array.set(array, i - 1, v);
		}

		return array;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection toCollection(Class<?> type, ArrayElement value) {

		try {
            Collection collection;
			if (!type.isInterface()) {
				collection = (Collection) repository.getConstructor(type).newInstance();
			} else {
				collection = (Collection) repository.getConstructor(
						getClassFor(value.getEntries().get(0).getValueAsString())).newInstance();
			}

			for (int i = 1; i < value.getEntries().size(); i++)
				collection.add(fromDBObject(value.getEntries().get(i).getValueAsObject()));

            return collection;
		} catch (InvocationTargetException e) {
			throw new SpaceMongoException("Could not find default constructor for type: " + type.getName(), e);
		} catch (InstantiationException e) {
			throw new SpaceMongoException("Could not find default constructor for type: " + type.getName(), e);
		} catch (IllegalAccessException e) {
			throw new SpaceMongoException("Could not find default constructor for type: " + type.getName(), e);
		}
	}

	public Class<?> getClassFor(String type) {
		try {
			return Class.forName(type);
		} catch (ClassNotFoundException e) {
			throw new SpaceMongoException("Could not resolve type for type: "
					+ type, e);
		}
	}

	public Document toDBObject(Object document) {

		if (document == null)
			return null;

		if (document instanceof SpaceDocument)
			return toDBObjectDocument((SpaceDocument) document);

		return toDBObjectPojo(document);
	}

	private Document toDBObjectDocument(SpaceDocument document) {
		DocumentBuilder bson = BuilderFactory.start();

		Set<String> keys = document.getProperties().keySet();

		bson.add(TYPE, document.getTypeName());

		for (String property : keys) {

			Object value = document.getProperty(property);

			if (value == null)
				continue;

			if (spaceTypeDescriptor.getIdPropertyName().equals(property))
				property = _ID;

			bson.add(property, toObject(value));
		}

		return bson.build();
	}

	private Document toDBObjectPojo(Object pojo) {

		DocumentBuilder bson = BuilderFactory.start();

		Map<String, Method> getters = repository.getGetters(pojo.getClass());

		Class<?> type = pojo.getClass();

		bson.add(TYPE, type.getName());

		for (String property : getters.keySet()) {
			Object value = null;
			try {

				value = repository.getGetter(type, property).get(pojo);

				if (value == null)
					continue;

				if (spaceTypeDescriptor.getIdPropertyName().equals(property))
					property = _ID;

				bson.add(property, toObject(value));

			} catch (IllegalArgumentException e) {
				throw new SpaceMongoException("Argument is: " + value, e);
			} catch (IllegalAccessException e) {
				throw new SpaceMongoException("Can not access method", e);
			} catch (InvocationTargetException e) {
				throw new SpaceMongoException("Can not invoke method", e);
			}
		}

		return bson.build();
	}

	public Object toObject(Object property) {

		switch (type(property)) {

		case TYPE_CHAR:
		case TYPE_FLOAT:
		case TYPE_BYTE:
		case TYPE_BIGDECIMAL:
		case TYPE_BIGINT:
			return toSpectialType(property);
		case TYPE_OBJECT:
			SpaceDocument document = MongoDocumentObjectConverter.instance()
					.toSpaceDocument(property);

			return toDBObject(document);
		case TYPE_ENUM:
			return toEnum(property);
		case TYPE_ARRAY:
			return toArray(property);
		case TYPE_COLLECTION:
			return toCollection(property);
		case TYPE_MAP:
			return toMap(property);
		default:
			return property;
		}
	}

	private Object toEnum(Object property) {

		DocumentBuilder document = BuilderFactory.start();

		return document.add(TYPE, property.getClass().getName())
				.add(VALUE, property.toString()).build();
	}

	private Element[] toMap(Object property) {

		ArrayBuilder builder = BuilderFactory.startArray();
		@SuppressWarnings("rawtypes")
		Map map = (Map) property;

		builder.add(property.getClass().getName());

		for (Object key : map.keySet()) {
			builder.add(toObject(key));
			builder.add(toObject(map.get(key)));
		}

		return builder.build();
	}

	private Element[] toCollection(Object property) {
		ArrayBuilder builder = BuilderFactory.startArray();

		@SuppressWarnings("rawtypes")
		Collection collection = (Collection) property;

		builder.add(property.getClass().getName());

		for (Object e : collection) {
			builder.add(toObject(e));
		}

		return builder.build();
	}

	private Element[] toArray(Object property) {
		ArrayBuilder builder = BuilderFactory.startArray();

		int length = Array.getLength(property);

		builder.add(property.getClass().getName());

		for (int i = 0; i < length; i++) {
			Object obj = toObject(Array.get(property, i));
			setArray(builder, obj);
		}

		return builder.build();
	}

	private void setArray(ArrayBuilder builder, Object obj) {

		switch (type(obj)) {
		case TYPE_INT:
			builder.add(((Integer) obj).intValue());
			break;
		case TYPE_SHORT:
			builder.add(((Short) obj).intValue());
			break;
		case TYPE_LONG:
			builder.add(((Long) obj).longValue());
			break;
		case TYPE_DOUBLE:
			builder.add(((Double) obj).doubleValue());
			break;
		default:
			builder.add(obj);
			break;
		}
	}

	private Character toCharacter(Object value) {
		if (value == null)
			return null;

		if (value instanceof String)
			return new Character(((String) value).charAt(0));

		throw new IllegalArgumentException("invalid value for Character: "
				+ value);
	}

	private Object fromSpetialType(Document value) {
		String type = value.get(TYPE).getValueAsString();
		String val = value.get(VALUE).getValueAsString();

		if (BigInteger.class.getName().equals(type))
			return new BigInteger(val);
		else if (BigDecimal.class.getName().equals(type))
			return new BigDecimal(val);
		else if (Byte.class.getName().equals(type))
			return Byte.valueOf(val);
		else if (Float.class.getName().equals(type))
			return Float.valueOf(val);
		else if (Character.class.getName().equals(type))
			return toCharacter(val);

		throw new IllegalArgumentException("unkown value: " + value);
	}

	private Document toSpectialType(Object property) {
		DocumentBuilder document = BuilderFactory.start();

		return document.add(TYPE, property.getClass().getName())
				.add(VALUE, property.toString()).build();
	}
}
