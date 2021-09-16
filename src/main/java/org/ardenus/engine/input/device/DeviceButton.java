package org.ardenus.engine.input.device;

import java.util.Objects;

import org.ardenus.engine.input.button.Button;

/**
 * Represents a button that is present on an input device.
 * 
 * @see InputDevice
 */
public class DeviceButton extends Button {

	public final String name;

	/**
	 * Constructs a new {@code DeviceButton}.
	 * 
	 * @param name
	 *            the button name.
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}.
	 */
	public DeviceButton(String name) {
		this.name = Objects.requireNonNull(name, "name");
	}

}
