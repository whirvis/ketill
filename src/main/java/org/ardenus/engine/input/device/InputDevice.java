package org.ardenus.engine.input.device;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.ardenus.engine.input.Input;
import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.event.DeviceConnectEvent;
import org.ardenus.engine.input.device.event.DeviceDisconnectEvent;
import org.ardenus.engine.input.device.feature.DeviceFeature;
import org.ardenus.engine.input.device.feature.FeaturePresent;

/**
 * A device which can send and receive input data.
 * <p>
 * Examples of input devices include, but are not limited to: keyboards, mouses,
 * XBOX controllers, PlayStation controllers, etc. By default, an input device
 * only has support for buttons. However, depending on the implementation,
 * features like mouse coordinates, gyroscopes, etc. may be present.
 * <p>
 * <b>Note:</b> For an input device to work properly, it must be polled via
 * {@link #poll()} before querying any input information. It is recommended to
 * poll the device once on every application update.
 * 
 * @see FeaturePresent
 * @see DeviceAdapter
 */
public abstract class InputDevice {

	protected final DeviceAdapter<?> adapter;
	private final Map<DeviceFeature<?>, Object> features;
	private boolean wasConnected;

	/**
	 * Constructs a new {@code InputDevice} and registers all device feature
	 * fields annotated with {@link FeaturePresent @FeaturePresent}.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 * @throws InputException
	 *             if an input error occurs.
	 * @see #addFeature(DeviceFeature)
	 */
	public InputDevice(DeviceAdapter<?> adapter) {
		this.adapter = Objects.requireNonNull(adapter);
		this.features = new HashMap<>();
		this.loadFeatures();
	}

	/**
	 * Returns if a feature is registered to this input device.
	 * 
	 * @param feature
	 *            the feature to check for.
	 * @return {@code true} if {@code feature} is registered, {@code false}
	 *         otherwise.
	 */
	public boolean hasFeature(DeviceFeature<?> feature) {
		if (feature != null) {
			return features.containsKey(feature);
		}
		return false;
	}

	/**
	 * Returns all features registered to this input device.
	 * 
	 * @return all features registered to this input device.
	 */
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
	 * Returns the current value of a device feature.
	 * 
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
					"no such feature \"" + feature.name() + "\"");
		}
		return value;
	}

	/**
	 * Returns if this input device is still connected.
	 * 
	 * @return {@code true} if this input device is still connected,
	 *         {@code false} otherwise.
	 */
	public boolean isConnected() {
		return adapter.isConnected();
	}

	/**
	 * Polls this input device.
	 * <p>
	 * Polling an input device is necessary for retrieving up to date input
	 * information. By default, only the states of registered analogs and
	 * buttons will be updated. However, more information may be polled
	 * depending on the implementation.
	 */
	public void poll() {
		adapter.poll();

		boolean connected = this.isConnected();
		if (!wasConnected && connected) {
			Input.sendEvent(new DeviceConnectEvent(this));
		} else if (wasConnected && !connected) {
			Input.sendEvent(new DeviceDisconnectEvent(this));
		}
		this.wasConnected = connected;

		for (Entry<DeviceFeature<?>, Object> entry : features.entrySet()) {
			adapter.update(entry.getKey(), entry.getValue());
		}
	}

}
