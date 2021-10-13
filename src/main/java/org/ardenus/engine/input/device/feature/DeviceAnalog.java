package org.ardenus.engine.input.device.feature;

import java.util.Objects;

import org.ardenus.engine.input.Analog;
import org.ardenus.engine.input.device.InputDevice;

/**
 * Represents an analog input that is present on an input device.
 *
 * @param <T>
 *            the value type.
 * @see InputDevice
 */
public abstract class DeviceAnalog<T> extends Analog<T>
		implements DeviceFeature<T> {

	private final String id;

	/**
	 * Constructs a new {@code DeviceAnalog}.
	 * 
	 * @param type
	 *            the value type class.
	 * @param id
	 *            the analog input ID.
	 * @throws NullPointerException
	 *             if {@code type} or {@code id} are {@code null}.
	 */
	public DeviceAnalog(Class<T> type, String id) {
		super(type);
		this.id = Objects.requireNonNull(id, "id");
	}

	@Override
	public final String id() {
		return this.id;
	}

	@Override
	public final T initial() {
		return this.zero();
	}

}
