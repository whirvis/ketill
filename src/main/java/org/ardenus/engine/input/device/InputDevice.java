package org.ardenus.engine.input.device;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.DeviceFeature;
import org.ardenus.engine.input.device.feature.FeaturePresent;
import org.ardenus.engine.input.device.feature.monitor.ConnectionMonitor;
import org.ardenus.engine.input.device.feature.monitor.FeatureMonitor;

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
 * @see DeviceAdapter
 * @see DeviceFeature
 * @see FeaturePresent
 * @see FeatureMonitor
 */
public abstract class InputDevice {

	public final String id;
	protected final DeviceAdapter<?> adapter;
	private final Set<FeatureMonitor> monitors;
	private final Map<DeviceFeature<?>, Object> features;

	/**
	 * Constructs a new {@code InputDevice} and registers all device feature
	 * fields annotated with {@link FeaturePresent @FeaturePresent}.
	 * 
	 * @param id
	 *            the device ID.
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code input} or {@code adapter} are {@code null}.
	 * @throws InputException
	 *             if an input error occurs.
	 * @see #addFeature(DeviceFeature)
	 */
	public InputDevice(String id, DeviceAdapter<?> adapter) {
		this.id = Objects.requireNonNull(id, "id");

		this.adapter = Objects.requireNonNull(adapter);
		this.monitors = new HashSet<>();
		this.features = new HashMap<>();
		this.loadFeatures();

		this.addMonitor(new ConnectionMonitor(this));
	}

	/**
	 * Adds a feature monitor to this input device.
	 * 
	 * @param monitor
	 *            the monitor to add.
	 * @throws NullPointerException
	 *             if {@code monitor} is {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code monitor} is not assigned to this input device.
	 */
	protected void addMonitor(FeatureMonitor monitor) {
		Objects.requireNonNull(monitor, "monitor");
		if (!monitor.isAssignedTo(this)) {
			throw new IllegalArgumentException(
					"monitor not assigned to this device");
		}

		if (!monitors.contains(monitor)) {
			monitors.add(monitor);
			for (DeviceFeature<?> feature : features.keySet()) {
				monitor.monitor(feature);
			}
		}
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
	 * Registers a device feature to this input device.
	 * <p>
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
	 *             if {@code feature} is not supported.
	 */
	protected void addFeature(DeviceFeature<?> feature) {
		Objects.requireNonNull(feature, "feature");
		if (!features.containsKey(feature)) {
			features.put(feature, feature.initial());
			for (FeatureMonitor monitor : monitors) {
				monitor.monitor(feature);
			}
		}
	}

	private void loadFeatures() {
		for (Field field : this.getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(FeaturePresent.class)) {
				continue;
			}

			/*
			 * Require that all present features be public. This is to ensure
			 * that they are accessible to this class. Not to mention, it makes
			 * no sense as to why a feature field would be hidden. Their entire
			 * purpose is to make it easier to fetch the value of a device
			 * feature!
			 */
			int mods = field.getModifiers();
			if (!Modifier.isPublic(mods)) {
				throw new InputException("device feature with name \""
						+ field.getName() + " in class "
						+ this.getClass().getName() + "must be public");
			}

			try {
				boolean statik = Modifier.isStatic(mods);
				Object obj = field.get(statik ? null : this);
				DeviceFeature<?> feature = (DeviceFeature<?>) obj;
				if (this.hasFeature(feature)) {
					throw new InputException("device feature already mapped");
				}
				this.addFeature(feature);
			} catch (IllegalAccessException e) {
				throw new InputException("failure to access", e);
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
					"no such feature \"" + feature.id() + "\"");
		}
		return value;
	}

	/**
	 * @return {@code true} if this input device is still connected,
	 *         {@code false} otherwise.
	 */
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

		/* monitors should be updated after each feature */
		for (FeatureMonitor monitor : monitors) {
			monitor.update();
		}
	}

}
