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

import org.springframework.util.ReflectionUtils;

public class SetterField implements Setter {

	private Field field;

	public SetterField(Field field) {

		if (field == null)
			throw new IllegalArgumentException("field can not be null");

		this.field = field;
	}

	public final synchronized void invokeSetter(Object target, Object arg0) {

		boolean access = field.isAccessible();
		try {
			field.setAccessible(true);
			ReflectionUtils.setField(field, target, arg0);
		} finally {
			field.setAccessible(access);
		}
	}

	public synchronized Class<?> getType() {
		return field.getType();
	}
}
