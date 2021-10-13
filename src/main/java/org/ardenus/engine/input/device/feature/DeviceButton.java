package org.ardenus.engine.input.device.feature;

import java.util.Objects;

import org.ardenus.engine.input.Button;
import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.InputDevice;

/**
 * Represents a button that is present on an input device.
 * 
 * @see InputDevice
 */
public class DeviceButton extends Button implements DeviceFeature<Button1bc> {

	private final String name;
	public final Direction direction;

	/**
	 * Constructs a new {@code DeviceButton}.
	 * 
	 * @param name
	 *            the button name.
	 * @param direction
	 *            the direction this button represents. A {@code null} value is
	 *            permitted, and indicates that this button does not represent a
	 *            direction.
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}.
	 */
	public DeviceButton(String name, Direction direction) {
		this.name = Objects.requireNonNull(name, "name");
		this.direction = direction;
	}

	/**
	 * Constructs a new {@code DeviceButton}.
	 * 
	 * @param name
	 *            the button name.
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}.
	 */
	public DeviceButton(String name) {
		this(name, null);
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
