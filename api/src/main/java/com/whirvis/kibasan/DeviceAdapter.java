package com.whirvis.kibasan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;
import java.util.*;

/**
 * An adapter which maps input for an {@link InputDevice}.
 * <p>
 * The purpose of a device adapter is to map data from a source (such as GLFW or
 * XInput) to an input device. This allows the same input device to be used with
 * different implementations. The use of adapters brings portability while also
 * providing a way to enable extra features, such as rumble or gyroscopes.
 * <p>
 * <b>Note:</b> For a device adapter to work properly, it must be polled via
 * {@link #poll()} before querying any input information. It is recommended to
 * poll the adapter once on every application update. Instances of
 * {@code InputDevice} automatically poll their respective adapters when they
 * are polled.
 *
 * @param <I>
 *            the input device type.
 * @see FeatureMapping
 * @see FeatureAdapter
 */
public abstract class DeviceAdapter<I extends InputDevice> {

	protected final Logger log;

	private final Map<Class<?>, Method> adapters;
	private final Map<Class<?>, Class<?>> adapterHierarchy;
	private final Map<DeviceFeature<?>, FeatureMapping<?>> mappings;
	private final Set<DeviceFeature<?>> absentMappings;

	/**
	 * All feature mapping fields annotated with {@link FeatureMapping} will be
	 * registered by this constructor. All feature adapter methods must be
	 * marked with {@link FeatureAdapter}.
	 */
	public DeviceAdapter() {
		this.log = LogManager.getLogger(this.getClass());

		this.adapters = new HashMap<>();
		this.adapterHierarchy = new HashMap<>();
		this.loadFeatureAdapters();

		this.mappings = new HashMap<>();
		this.absentMappings = new HashSet<>();
		this.loadInputMappings();
	}

	private void loadFeatureAdapters() {
		Class<?> clazz = this.getClass();
		for (Method method : Reflection.getAllMethods(clazz)) {
			FeatureAdapter adapter = method.getAnnotation(FeatureAdapter.class);
			if (adapter == null) {
				continue;
			}

			String methodDesc = "@" + FeatureAdapter.class.getSimpleName()
					+ " method \"" + method.getName() + "\" in class "
					+ clazz.getName();

			/*
			 * For the sake of simplicity, all feature adapters must be public
			 * and cannot be static. This ensures they behave like instanced
			 * methods and are accessible by this class.
			 */
			int mod = method.getModifiers();
			if (!Modifier.isPublic(mod)) {
				throw new InputException(methodDesc + " must be public");
			} else if (Modifier.isStatic(mod)) {
				throw new InputException(methodDesc + " cannot be static");
			}

			/*
			 * Feature adapters update an already existing value, they do not
			 * return a new one. This is to cut down on object allocations. If
			 * an adapter method does not return void, throw an exception. This
			 * was likely a mistake by the programmer.
			 */
			if (method.getReturnType() != void.class) {
				throw new InputException(methodDesc + " must return void");
			}

			/*
			 * Adapter methods take two parameters. The first parameter is the
			 * feature mapping they are going to update. The second parameter is
			 * the current value of the feature, which the method updates.
			 */
			Parameter[] params = method.getParameters();
			if (params.length != 2) {
				throw new InputException("expecting two parameters");
			}

			Class<?> adapterType = params[0].getType();
			if (!FeatureMapping.class.isAssignableFrom(adapterType)) {
				throw new InputException("first parameter of " + methodDesc
						+ " must be assignable from "
						+ FeatureMapping.class.getSimpleName());
			}

			adapters.put(adapterType, method);
		}

		/*
		 * At runtime, the appropriate feature adapter method is chosen based on
		 * the highest order class assignable from the feature mapping type. If
		 * two or more classes are assignable to the same feature adapter, which
		 * adapter method to use will become ambiguous.
		 */
		for (Class<?> higher : adapters.keySet()) {
			for (Class<?> lower : adapters.keySet()) {
				if (higher != lower && higher.isAssignableFrom(lower)) {
					throw new InputException("ambigous hierarchy for "
							+ "feature adapters, cannot choose between "
							+ adapters.get(higher).getName() + " and "
							+ adapters.get(lower).getName() + " for "
							+ "mapping of type" + higher.getName());
				}
			}
		}
	}

	private void requireAdapter(Class<?> mappingClazz) {
		if (adapterHierarchy.containsKey(mappingClazz)) {
			return;
		}

		/*
		 * Search for an appropriate adapter method and cache it for later
		 * calls. The first adapter method whose type is assignable from the
		 * feature mapping will be chosen. The loadFeatureAdapters() method
		 * guarantees there will only be one class assignable to the mapping.
		 */
		for (Class<?> candidate : adapters.keySet()) {
			if (candidate.isAssignableFrom(mappingClazz)) {
				adapterHierarchy.put(mappingClazz, candidate);
				break;
			}
		}

		if (!adapterHierarchy.containsKey(mappingClazz)) {
			throw new InputException(
					"no adapter for " + mappingClazz.getName());
		}
	}

	public boolean hasMapping(DeviceFeature<?> feature) {
		if (feature == null) {
			return false;
		}
		return mappings.containsKey(feature);
	}

	public boolean hasMapping(FeatureMapping<?> mapping) {
		if (mapping == null) {
			return false;
		}
		return this.hasMapping(mapping.feature);
	}

	protected FeatureMapping<?> getMapping(DeviceFeature<?> feature) {
		if (feature == null) {
			return null;
		}
		return mappings.get(feature);
	}

	/**
	 * @param mapping
	 *            the mapping to register.
	 * @return this device adapter.
	 * @throws NullPointerException
	 *             if {@code mapping} is {@code null}.
	 * @throws InputException
	 *             if no adapter exists for {@code mapping}.
	 */
	protected DeviceAdapter<I> map(FeatureMapping<?> mapping) {
		Objects.requireNonNull(mapping, "mapping");

		/* this was likely this done on mistake */
		DeviceFeature<?> feature = mapping.feature;
		if (this.hasMapping(feature)) {
			throw new InputException(
					"feature \"" + feature.id() + "\" already mapped");
		}

		this.requireAdapter(mapping.getClass());
		mappings.put(mapping.feature, mapping);
		return this;
	}

	/**
	 * This method is a shorthand for {@link #map(FeatureMapping)}, with each
	 * value of {@code mappings} being passed as the argument for
	 * {@code mapping}.
	 * 
	 * @param mappings
	 *            the mappings to register.
	 * @return this device adapter.
	 * @throws NullPointerException
	 *             if {@code mappings} or one of its elements are {@code null}.
	 * @throws InputException
	 *             if no adapter exists for {@code mappings}.
	 */
	protected DeviceAdapter<I> map(FeatureMapping<?>... mappings) {
		Objects.requireNonNull(mappings, "mappings");
		for (FeatureMapping<?> mapping : mappings) {
			this.map(mapping);
		}
		return this;
	}

	/**
	 * @param feature
	 *            the feature whose mapping to unregister.
	 * @return {@code true} if the mapping for {@code feature} was unregistered
	 *         from this adapter, {@code false} otherwise.
	 */
	protected boolean unmap(DeviceFeature<?> feature) {
		if (feature == null) {
			return false;
		}
		FeatureMapping<?> unmapped = mappings.remove(feature);
		return unmapped != null;
	}

	/**
	 * @param mapping
	 *            the mapping to unregister.
	 * @return {@code true} if {@code mapping} was unregistered from this
	 *         adapter, {@code false} otherwise.
	 */
	protected boolean unmap(FeatureMapping<?> mapping) {
		if (mapping == null) {
			return false;
		}
		Iterator<FeatureMapping<?>> buttonsI = mappings.values().iterator();
		while (buttonsI.hasNext()) {
			FeatureMapping<?> value = buttonsI.next();
			if (mapping == value) {
				buttonsI.remove();
				return true;
			}
		}
		return false;
	}

	private void loadInputMappings() {
		Class<?> clazz = this.getClass();
		for (Field field : Reflection.getAllFields(clazz)) {
			if (!field.isAnnotationPresent(AdapterMapping.class)) {
				continue;
			}

			String fieldDesc = "@" + AdapterMapping.class.getSimpleName()
					+ " field \"" + field.getName() + "\" in class "
					+ clazz.getName();

			Class<?> type = field.getType();
			if (!FeatureMapping.class.isAssignableFrom(type)) {
				throw new InputException(fieldDesc + " must be assignable from "
						+ FeatureMapping.class.getSimpleName());
			}

			/*
			 * Require that all adapter mappings be public to ensure that they
			 * are accessible to this class. Allowing @AdapterMapping fields to
			 * be hidden provides more hassle than it does utility.
			 */
			int mods = field.getModifiers();
			if (!Modifier.isPublic(mods)) {
				throw new InputException(fieldDesc + " must be public");
			}

			try {
				boolean statik = Modifier.isStatic(mods);
				Object obj = field.get(statik ? null : this);
				this.map((FeatureMapping<?>) obj);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public abstract boolean isConnected();

	/**
	 * For this method to work properly, {@code feature} must have been given a
	 * mapping and this class must have an appropriate feature adapter. If no
	 * mapping was specified, this method will be a no-op. If no adapter for the
	 * mapping type was registered, an {@code InputException} will be thrown.
	 * 
	 * @param feature
	 *            the feature to query.
	 * @param value
	 *            the container to update.
	 * @throws NullPointerException
	 *             if {@code feature} or {@code value} are {@code null}.
	 * @throws InputException
	 *             if the mapping type for {@code feature} has no adapter; if
	 *             {@code value} is of an invalid type for the adapter.
	 * @see #map(FeatureMapping)
	 * @see FeatureAdapter
	 */
	public void update(DeviceFeature<?> feature, Object value) {
		Objects.requireNonNull(feature, "feature");
		Objects.requireNonNull(value, "value");

		FeatureMapping<?> mapping = mappings.get(feature);
		if (mapping == null) {
			if (!absentMappings.contains(feature)) {
				log.warn("no mapping for feature \"" + feature.id() + "\"");
				absentMappings.add(feature);
			}
			return;
		}

		Class<?> adapterClazz = adapterHierarchy.get(mapping.getClass());
		if (adapterClazz == null) {
			throw new InputException(
					"no adapter for feature \"" + feature.id() + "\"");
		}

		Method adapter = adapters.get(adapterClazz);
		try {
			adapter.invoke(this, mapping, value);
		} catch (IllegalArgumentException e) {
			/*
			 * If the value parameter type is not assignable from the passed
			 * value type, that means the IllegalArgumentException was likely
			 * caused by that. Otherwise, it was caused by something else.
			 */
			Parameter valueParam = adapter.getParameters()[1];
			if (valueParam.getType().isAssignableFrom(value.getClass())) {
				throw new InputException("illegal value type for adapter, "
						+ "expecting " + valueParam.getType().getName()
						+ " (got " + value.getClass().getName() + ")", e);
			} else {
				throw e;
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Polling a device adapter is not always necessary for retrieving up to
	 * date input information. However, this is only down to how the adapter
	 * retrieves information from its source. As such, it is highly recommended
	 * to always poll the adapter; even if the method may be a no-op.
	 */
	public abstract void poll();

}
