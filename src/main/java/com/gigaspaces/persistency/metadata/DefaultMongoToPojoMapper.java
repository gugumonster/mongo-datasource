package com.gigaspaces.persistency.metadata;

import java.util.HashMap;
import java.util.Map;

import com.gigaspaces.metadata.SpacePropertyDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.mongodb.DBObject;

public class DefaultMongoToPojoMapper implements Mappper<DBObject, Object> {

	private static final String _ID2 = "_id";
	private SpaceTypeDescriptor spaceTypeDescriptor;
	private Class<?> clazz;

	private final Map<String, Setter> setterCache = new HashMap<String, Setter>();
	private final DefaultSetterFactory factory = new DefaultSetterFactory();

	public DefaultMongoToPojoMapper(SpaceTypeDescriptor spaceTypeDescriptor)
			throws ClassNotFoundException {

		if (spaceTypeDescriptor == null)
			throw new IllegalArgumentException(
					"spaceTypeDescriptior can notnbe null");

		this.spaceTypeDescriptor = spaceTypeDescriptor;

		this.clazz = Class.forName(spaceTypeDescriptor.getTypeName());
	}

	public Object maps(DBObject bson) {

		Object pojo = null;

		try {
			pojo = create();

			mapIdProperty(bson, pojo);

			mapFixedProperties(bson, pojo);

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void mapFixedProperties(DBObject bson, Object pojo) {

		for (String key : bson.keySet()) {
			SpacePropertyDescriptor sp = spaceTypeDescriptor
					.getFixedProperty(key);

			if (sp == null
					|| spaceTypeDescriptor.getIdPropertyName().equals(
							sp.getName()))
				continue;

			Setter setter = getSetter(sp.getName());

			Object arg0 = bson.get(key);

			if (sp.getType().isEnum()) {
				DBObject obj = (DBObject) arg0;
				String value = (String) obj.get(obj.keySet().iterator().next());
				arg0 = Enum.valueOf((Class<Enum>) sp.getType(), value);
			}

			setter.invokeSetter(pojo, arg0);
		}
	}

	private void mapIdProperty(DBObject bson, Object pojo) {

		String propertyId = spaceTypeDescriptor.getIdPropertyName();

		Setter idSetter = getSetter(propertyId);

		idSetter.invokeSetter(pojo, bson.get(_ID2));
	}

	protected Setter getSetter(String propertyId) {
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
