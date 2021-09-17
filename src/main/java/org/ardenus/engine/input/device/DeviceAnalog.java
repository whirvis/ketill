package org.ardenus.engine.input.device;

import org.ardenus.engine.input.Analog;

/**
 * Represents an analog input that is present on an input device.
 *
 * @param <T>
 *            the value type.
 * @see InputDevice
 */
public abstract class DeviceAnalog<T> extends Analog<T> {

	public final String name;

	/**
	 * Constructs a new {@code DeviceAnalog}.
	 * 
	 * @param type
	 *            the value type class.
	 * @param name
	 *            the analog input name.
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}.
	 */
	public DeviceAnalog(Class<T> type, String name) {
		super(type);
		this.name = name;
	}

}
