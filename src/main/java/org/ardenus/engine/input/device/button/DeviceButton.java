package org.ardenus.engine.input.device.button;

import java.util.Objects;

import org.ardenus.engine.input.Button;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.InputSource;

/**
 * Represents a button that is present on an input device.
 * 
 * @see InputDevice
 */
public class DeviceButton extends Button implements InputSource<Button1bc> {

	private final String name;

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

	@Override
	public final String name() {
		return this.name;
	}

	@Override
	public Button1bc initial() {
		return new Button1b();
	}

}
