package org.ardenus.engine.input.device.analog;

import org.ardenus.engine.input.Analog;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.InputSource;

/**
 * Represents an analog input that is present on an input device.
 *
 * @param <T>
 *            the value type.
 * @see InputDevice
 */
public abstract class DeviceAnalog<T> extends Analog<T>
		implements InputSource<T> {

	private final String name;

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

	@Override
	public final String name() {
		return this.name;
	}

	@Override
	public final T initial() {
		return this.zero();
	}

}
