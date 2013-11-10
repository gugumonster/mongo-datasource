package com.gigaspaces.persistency.utest.metadata;

import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Test;

import com.gigaspaces.internal.reflection.IGetterMethod;
import com.gigaspaces.internal.reflection.ISetterMethod;
import com.gigaspaces.internal.utils.Assert;
import com.gigaspaces.persistency.metadata.PojoRepository;

public class PojoRepositoryTest {

	private static final int NUMBER = 27017;
	private static final String MY_NAME = "My Name";

	public static class PojoA {
		private String nameA;

		public PojoA() {
		}

		public String getNameA() {
			return nameA;
		}

		public void setNameA(String nameA) {
			this.nameA = nameA;
		}

	}

	public static class PojoB extends PojoA {
		private int numberB;

		public PojoB() {
		}

		public int getNumberB() {
			return numberB;
		}

		public void setNumberB(int numberB) {
			this.numberB = numberB;
		}
	}

	private PojoRepository repository;

	@Before
	public void before() {
		repository = new PojoRepository();

		repository.introcpect(PojoA.class);
		repository.introcpect(PojoB.class);

	}

	@Test
	public void testConstructor() throws InvocationTargetException,
			InstantiationException, IllegalAccessException {

		PojoA pojoA = (PojoA) repository.getConstructor(PojoA.class)
				.newInstance();
		Assert.notNull(pojoA);

		PojoB pojoB = (PojoB) repository.getConstructor(PojoB.class)
				.newInstance();

		Assert.notNull(pojoB);
	}

	@Test
	public void testGetter() throws InvocationTargetException,
			InstantiationException, IllegalAccessException {

		PojoB pojoB = (PojoB) repository.getConstructor(PojoB.class)
				.newInstance();

		IGetterMethod<Object> nameA = repository
				.getGetter(PojoB.class, "nameA");
		IGetterMethod<Object> numberB = repository.getGetter(PojoB.class,
				"numberB");

		Assert.isNull(nameA.get(pojoB));
		Assert.isTrue(numberB.get(pojoB).equals(0));

		pojoB.setNameA(MY_NAME);
		pojoB.setNumberB(NUMBER);

		Assert.isTrue(nameA.get(pojoB).equals(MY_NAME));
		Assert.isTrue(numberB.get(pojoB).equals(NUMBER));

	}

	@Test
	public void testSetter() throws InvocationTargetException,
			InstantiationException, IllegalAccessException {
		PojoB pojoB = (PojoB) repository.getConstructor(PojoB.class)
				.newInstance();

		ISetterMethod<Object> nameA = repository
				.getSetter(PojoB.class, "nameA");

		ISetterMethod<Object> numberB = repository.getSetter(PojoB.class,
				"numberB");

		nameA.set(pojoB, MY_NAME);
		numberB.set(pojoB, NUMBER);

		Assert.isTrue(pojoB.getNameA().equals(MY_NAME));
		Assert.isTrue(pojoB.getNumberB() == NUMBER);
	}

	@Test(expected = IllegalStateException.class)
	public void testUndefinePrperty() {
		repository.getSetter(PojoB.class, "nameAAAAAAAAAAAAAAAAAAAAAAAAA");
	}

}
