package com.whirvis.kibasan;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

/**
 * A device which can send and receive input data.
 * <p>
 * Examples of input devices include, but are not limited to: keyboards, mouses,
 * XBOX controllers, PlayStation controllers, etc. By design, an input device
 * does not support any device feature by default. While an input device will
 * usually accept any device feature it is given, it is up to the implementation
 * to provide shorthands for easy access and communication with them.
 * <p>
 * <b>Note:</b> For an input device to work properly, it must be polled via
 * {@link #poll()} before querying any input information. It is recommended to
 * poll the device once on every application update.
 *
 * @see DeviceSeeker
 * @see DeviceAdapter
 * @see DeviceFeature
 * @see FeaturePresent
 */
public abstract class InputDevice {

	public final String id;
	protected final DeviceAdapter<?> adapter;
	private final Map<DeviceFeature<?>, Object> features;

	/**
	 * All device feature fields annotated with {@link FeaturePresent} will be
	 * registered by this constructor.
	 * 
	 * @param id
	 *            the device ID.
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code id} or {@code adapter} are {@code null}.
	 * @see #addFeature(DeviceFeature)
	 */
	public InputDevice(String id, DeviceAdapter<?> adapter) {
		this.id = Objects.requireNonNull(id);
		this.adapter = Objects.requireNonNull(adapter);
		this.features = new HashMap<>();
		this.loadFeatures();
	}

	public boolean hasFeature(DeviceFeature<?> feature) {
		if (feature != null) {
			return features.containsKey(feature);
		}
		return false;
	}

	public Set<DeviceFeature<?>> getFeatures() {
		return Collections.unmodifiableSet(features.keySet());
	}

	/**
	 * When a feature is registered, it is stored alongside an instance of its
	 * initial state. If {@code feature} is already registered, its current
	 * state will not be reset to its initial value.
	 * <p>
	 * <b>Note:</b> This method can be called before {@code InputDevice} is
	 * finished constructing, as it is called by the {@link #loadFeatures()}
	 * method (which is called inside the constructor). As such, extending
	 * classes should take care to write code around this fact should they
	 * override this method.
	 * 
	 * @param feature
	 *            the feature to register.
	 * @throws NullPointerException
	 *             if {@code feature} is {@code null}.
	 * @throws InputException
	 *             if {@code feature} is already registered.
	 */
	protected void addFeature(DeviceFeature<?> feature) {
		Objects.requireNonNull(feature, "feature");
		if (this.hasFeature(feature)) {
			throw new InputException("feature already registered");
		}
		features.put(feature, feature.initial.get());
	}

	private void loadFeatures() {
		Class<?> clazz = this.getClass();
		for (Field field : Reflection.getAllFields(clazz)) {
			if (!field.isAnnotationPresent(FeaturePresent.class)) {
				continue;
			}

			String fieldDesc = "@" + FeaturePresent.class.getSimpleName()
					+ " field \"" + field.getName() + "\" in class "
					+ clazz.getName();

			Class<?> type = field.getType();
			if (!DeviceFeature.class.isAssignableFrom(type)) {
				throw new InputException(fieldDesc + " must be assignable from "
						+ DeviceFeature.class.getName());
			}

			/*
			 * Require that all present features be public to ensure that they
			 * are accessible to this class. It would make no sense for feature
			 * field annotated with @FeaturePresent to be hidden.
			 */
			int mods = field.getModifiers();
			if (!Modifier.isPublic(mods)) {
				throw new InputException(fieldDesc + " must be public");
			}

			try {
				boolean statik = Modifier.isStatic(mods);
				Object obj = field.get(statik ? null : this);
				this.addFeature((DeviceFeature<?>) obj);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @param <T>
	 *            the feature value type.
	 * @param feature
	 *            the feature whose state to fetch.
	 * @return the current value of {@code feature}.
	 * @throws NullPointerException
	 *             if {@code feature} is {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code feature} is not registered.
	 * @see #addFeature(DeviceFeature)
	 */
	@SuppressWarnings("unchecked")
	public <T> T getState(DeviceFeature<T> feature) {
		Objects.requireNonNull(feature, "feature");
		T value = (T) features.get(feature);
		if (value == null) {
			throw new IllegalArgumentException(
					"no such feature \"" + feature.id + "\"");
		}
		return value;
	}

	public boolean isConnected() {
		return adapter.isConnected();
	}

	/**
	 * Polling an input device is usually necessary for retrieving up to date
	 * input information (some implementations technically do not require it.)
	 * Nevertheless, it is recommended to poll all input devices once on each
	 * program update. The information that is updated on each poll is dependent
	 * on the input device and its implementation.
	 */
	public void poll() {
		adapter.poll();
		for (Entry<DeviceFeature<?>, Object> entry : features.entrySet()) {
			adapter.update(entry.getKey(), entry.getValue());
		}
	}

}
