package com.whirvis.kibasan;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility methods for working with the Java Reflection API.
 */
public final class Reflection {

	private Reflection() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a set containing all the fields inside a class as returned by
	 * both {@link Class#getDeclaredFields()} and {@link Class#getFields()}
	 * combined.
	 * 
	 * @param clazz
	 *            the class whose fields to fetch.
	 * @return all fields inside of {@code clazz}.
	 */
	public static Set<Field> getAllFields(Class<?> clazz) {
		Set<Field> fields = new HashSet<>();
		for (Field field : clazz.getDeclaredFields()) {
			fields.add(field);
		}
		for (Field field : clazz.getFields()) {
			fields.add(field);
		}
		return fields;
	}

	/**
	 * Returns a set containing all the methods inside a class as returned by
	 * both {@link Class#getDeclaredMethods()} and {@link Class#getMethods()}
	 * combined.
	 * 
	 * @param clazz
	 *            the class whose methods to fetch.
	 * @return all methods inside of {@code clazz}.
	 */
	public static Set<Method> getAllMethods(Class<?> clazz) {
		Set<Method> methods = new HashSet<>();
		for (Method field : clazz.getDeclaredMethods()) {
			methods.add(field);
		}
		for (Method field : clazz.getMethods()) {
			methods.add(field);
		}
		return methods;
	}

}
