package com.whirvis.kibasan;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.whirvis.kibasan.adapter.DeviceAdapter;
import com.whirvis.kibasan.feature.DeviceFeature;
import com.whirvis.kibasan.feature.FeaturePresent;
import com.whirvis.kibasan.seeker.DeviceSeeker;

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
 * @see DeviceId
 * @see DeviceSeeker
 * @see DeviceAdapter
 * @see DeviceFeature
 * @see FeaturePresent
 */
public abstract class InputDevice {

	/**
	 * @param clazz
	 *            the input device class.
	 * @return the ID, {@code null} if {@code clazz} has no {@link DeviceId}
	 *         annotation present.
	 */
	public static String getId(Class<? extends InputDevice> clazz) {
		if (clazz != null) {
			DeviceId id = clazz.getAnnotation(DeviceId.class);
			if (id != null) {
				return id.value();
			}
		}
		return null;
	}

	public final String id;
	protected final DeviceAdapter<?> adapter;
	private final Map<DeviceFeature<?>, Object> features;

	/**
	 * All device feature fields annotated with {@link FeaturePresent} will be
	 * registered by this constructor.
	 * 
	 * @param id
	 *            the device ID, should be {@code null} if the {@link DeviceId}
	 *            annotation is present for this class.
	 * @param adapter
	 *            the device adapter.
	 * @throws IllegalArgumentException
	 *             if the {@link DeviceId} annotation is present and {@code id}
	 *             is not {@code null}.
	 * @throws NullPointerException
	 *             if no ID was specified for this device; if {@code adapter} is
	 *             {@code null}.
	 * @see #addFeature(DeviceFeature)
	 */
	public InputDevice(String id, DeviceAdapter<?> adapter) {
		/*
		 * It would not make logical sense for the device to have both a static
		 * ID and an instance ID specified at construction. Even if they match,
		 * it is likely that this was done by mistake. As such, throw an error
		 * to force the user to pick one or the other.
		 */
		String statikId = getId(this.getClass());
		if (statikId != null && id != null) {
			throw new IllegalArgumentException(
					"cannot have a static ID and instance ID");
		} else if (statikId != null) {
			this.id = statikId;
		} else if (id != null) {
			this.id = id;
		} else {
			throw new NullPointerException("missing ID");
		}

		this.adapter = Objects.requireNonNull(adapter);
		this.features = new HashMap<>();
		this.loadFeatures();
	}

	/**
	 * When using this constructor, the device ID is determined by the
	 * {@link DeviceId} annotation, which must be present for this class.
	 * <p>
	 * All device feature fields annotated with {@link FeaturePresent} will be
	 * registered by this constructor.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if no ID was specified for this device; if {@code adapter} is
	 *             {@code null}.
	 * @see #addFeature(DeviceFeature)
	 */
	public InputDevice(DeviceAdapter<?> adapter) {
		this(null, adapter);
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
		features.put(feature, feature.initial());
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
					"no such feature \"" + feature.id() + "\"");
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
