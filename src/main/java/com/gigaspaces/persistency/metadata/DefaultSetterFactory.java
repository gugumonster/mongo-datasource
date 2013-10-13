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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

import com.gigaspaces.metadata.SpacePropertyDescriptor;

public class DefaultSetterFactory {

	public Setter create(Class<?> clazz,
			SpacePropertyDescriptor spacePropertyDescriptor) {

		return create(clazz, spacePropertyDescriptor.getName());
	}

	private Method getSetterMethod(Class<?> clazz, String key) {

		String setterName = "set" + key.substring(0, 1).toUpperCase()
				+ key.substring(1);

		Method method = ReflectionUtils.findMethod(clazz, setterName);

		return method;
	}

	public Setter create(Class<?> clazz, String key) {

		Method method = getSetterMethod(clazz, key);

		if (method == null) {
			Field field = ReflectionUtils.findField(clazz, key);

			return new SetterField(field);
		}

		return new SetterMehtod(method);

	}
}
