package com.gigaspaces.persistency.metadata;

import java.util.HashMap;
import java.util.Map;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpacePropertyDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.mongodb.DBObject;

/**
 * @author Shadi Massalha
 * 
 */
public class DefaultMongoToPojoMapper extends MetadataUtils implements
		Mapper<DBObject, Object> {

	private static final String _ID2 = "_id";
	private SpaceTypeDescriptor spaceTypeDescriptor;
	private Class<?> clazz;

	private final Map<String, Setter> setterCache = new HashMap<String, Setter>();
	private final DefaultSetterFactory factory = new DefaultSetterFactory();

	public DefaultMongoToPojoMapper(SpaceTypeDescriptor spaceTypeDescriptor) {

		if (spaceTypeDescriptor == null)
			throw new IllegalArgumentException(
					"spaceTypeDescriptior can notnbe null");

		this.spaceTypeDescriptor = spaceTypeDescriptor;

		if (!spaceTypeDescriptor.supportsDynamicProperties())
			try {
				this.clazz = Class.forName(this.spaceTypeDescriptor
						.getTypeName());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public synchronized Object maps(DBObject bson) {

		if (this.spaceTypeDescriptor.supportsDynamicProperties())
			return mapDocument(bson);

		return mapPojo(bson);
	}

	private Object mapDocument(DBObject bson) {
		SpaceDocument doc = new SpaceDocument(spaceTypeDescriptor.getTypeName());

		for (String key : bson.keySet()) {
			Object d = bson.get(key);
			if (d instanceof DBObject) {
				doc.setProperty(key, mapDocument((DBObject) d));
			} else {
				if ("_id".equals(key)) {
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
			pojo = create();

			mapIdProperty(bson, pojo);

			mapFixedProperties(bson, pojo);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
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

			Setter setter = getSetter(sp.getName());

			Object arg0 = prepareArgument(bson, key, sp);

			setter.invokeSetter(pojo, arg0);
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

		Setter idSetter = getSetter(propertyId);

		Object arg0 = prepareArgument(bson, _ID2,
				spaceTypeDescriptor.getFixedProperty(propertyId));

		idSetter.invokeSetter(pojo, arg0);
	}

	private Setter getSetter(String propertyId) {
		SpacePropertyDescriptor spacePropertyDescriptor = spaceTypeDescriptor
				.getFixedProperty(propertyId);

		Setter setter = setterCache.get(propertyId);

		if (setter == null) {
			setter = factory.create(clazz, spacePropertyDescriptor);

			setterCache.put(propertyId, setter);
		}

		return setter;
	}

	private Object create() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {

		return clazz.newInstance();
	}
}
