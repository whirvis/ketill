package org.ardenus.engine.input.device.controller;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.DeviceButton;

/**
 * Represents a button that is present on a controller.
 * 
 * @see Controller
 * @see Direction
 */
public class ControllerButton extends DeviceButton {

	public final Direction direction;

	/**
	 * Constructs a new {@code ControllerButton}.
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
	public ControllerButton(String name, Direction direction) {
		super(name);
		this.direction = direction;
	}

	/**
	 * Constructs a new {@code ControllerButton} which does not represent a
	 * direction.
	 * 
	 * @param name
	 *            the button name.
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}.
	 */
	public ControllerButton(String name) {
		this(name, null);
	}

}
