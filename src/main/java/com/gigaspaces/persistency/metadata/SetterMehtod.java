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

import org.springframework.util.ReflectionUtils;

public class SetterMehtod implements Setter {

	private Method setter;

	public SetterMehtod(Method setter) {
		if (setter == null)
			throw new IllegalArgumentException("setter method can not be null");

		this.setter = setter;
	}

	public synchronized void invokeSetter(Object target, Object arg0) {
		try {
			ReflectionUtils.invokeMethod(setter, target, arg0);
		} catch (IllegalArgumentException ex) {
			System.err.println(ex);
		}

	}

	public synchronized Class<?> getType() {
		return setter.getParameterTypes()[0];
	}
}
