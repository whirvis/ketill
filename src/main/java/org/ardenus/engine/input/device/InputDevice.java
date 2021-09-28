package org.ardenus.engine.input.device;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;

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
 * @see SupportSources
 * @see SourcePresent
 * @see DeviceAdapter
 */
public abstract class InputDevice {

	protected final DeviceAdapter<?> adapter;
	private final Map<InputSource<?>, Object> sources;

	/**
	 * Constructs a new {@code InputDevice} and registers all input source
	 * fields annotated with {@link SourcePresent @SourcePresent}.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 * @throws InputException
	 *             if an input error occurs.
	 * @see #addSource(InputSource)
	 */
	public InputDevice(DeviceAdapter<?> adapter) {
		this.adapter = Objects.requireNonNull(adapter);
		this.sources = new HashMap<>();
		this.loadInputSources();
	}

	/**
	 * Returns if a source is registered to this input device.
	 * 
	 * @param source
	 *            the input source to check for.
	 * @return {@code true} if {@code source} is registered, {@code false}
	 *         otherwise.
	 */
	public boolean hasSource(InputSource<?> source) {
		if (source != null) {
			return sources.containsKey(source);
		}
		return false;
	}

	/**
	 * Returns all sources registered to this input device.
	 * 
	 * @return all sources registered to this input device.
	 */
	public Set<InputSource<?>> getSources() {
		return Collections.unmodifiableSet(sources.keySet());
	}

	/**
	 * Registers an input source to this input device.
	 * <p>
	 * When a source is registered, it is stored alongside an instance of its
	 * initial state. If {@code source} is already registered, its current state
	 * will not be reset to its initial value.
	 * <p>
	 * <b>Note:</b> This method can be called before {@code InputDevice} is
	 * finished constructing, as it is called by the {@link #loadInputSources()}
	 * method (which is called inside the constructor). As such, extending
	 * classes should take care to write code around this fact should they
	 * override this method.
	 * 
	 * @param source
	 *            the input source to register.
	 * @throws NullPointerException
	 *             if {@code source} is {@code null}.
	 * @throws InputException
	 *             if {@code source} is not supported.
	 * @see SupportSources
	 */
	protected void addSource(InputSource<?> source) {
		Objects.requireNonNull(source, "source");
		if (!sources.containsKey(source)) {
			sources.put(source, source.initial());
		}
	}

	private void loadInputSources() {
		for (Field field : this.getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(SourcePresent.class)) {
				continue;
			}

			/*
			 * Require that all present sources be public. This is to ensure
			 * that they are accessible to this class. Not to mention, it makes
			 * no sense as to why an input source field would be hidden. Their
			 * entire purpose is to make it easier to fetch the value of an
			 * input source!
			 */
			int mods = field.getModifiers();
			if (!Modifier.isPublic(mods)) {
				throw new InputException("input source with name \""
						+ field.getName() + " in class "
						+ this.getClass().getName() + "must be public");
			}

			try {
				boolean statik = Modifier.isStatic(mods);
				Object obj = field.get(statik ? null : this);
				InputSource<?> source = (InputSource<?>) obj;
				if (this.hasSource(source)) {
					throw new InputException("input source already mapped");
				}
				this.addSource(source);
			} catch (IllegalAccessException e) {
				throw new InputException("failure to access", e);
			}
		}
	}

	/**
	 * Returns the current value of an input source.
	 * 
	 * @param <T>
	 *            the input source value type.
	 * @param source
	 *            the input source whose value to fetch.
	 * @return the current value of {@code source}.
	 * @throws NullPointerException
	 *             if {@code source} is {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code source} is not registered.
	 * @see #addSource(InputSource)
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(InputSource<T> source) {
		Objects.requireNonNull(source, "source");
		T value = (T) sources.get(source);
		if (value == null) {
			throw new IllegalArgumentException(
					"no such source \"" + source.name() + "\"");
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
		for (Entry<InputSource<?>, Object> entry : sources.entrySet()) {
			adapter.update(entry.getKey(), entry.getValue());
		}
	}

}
