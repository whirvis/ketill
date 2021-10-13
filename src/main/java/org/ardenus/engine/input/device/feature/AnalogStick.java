package org.ardenus.engine.input.device.feature;

import org.ardenus.engine.input.Direction;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Represents an analog stick on an input device.
 */
public class AnalogStick extends DeviceAnalog<Vector3fc> {

	private static final float STICK_PRESS = 2.0F / 3.0F;

	/**
	 * Returns if a vector points towards a direction.
	 * 
	 * @param pos
	 *            the analog stick position.
	 * @param direction
	 *            the direction to check for.
	 * @return {@code true} if {@code pos} is pointing towards
	 *         {@code direction}, {@code false} otherwise.
	 */
	public static boolean isPressed(Vector3fc pos, Direction direction) {
		if (pos == null) {
			return false;
		}

		switch (direction) {
		case UP:
			return pos.y() >= STICK_PRESS;
		case DOWN:
			return pos.y() <= -STICK_PRESS;
		case LEFT:
			return pos.x() <= -STICK_PRESS;
		case RIGHT:
			return pos.x() >= STICK_PRESS;
		}

		throw new UnsupportedOperationException(
				"unknown direction, this should not occurr");
	}

	public final DeviceButton zButton;

	/**
	 * Constructs a new {@code AnalogStick}.
	 * 
	 * @param name
	 *            the analog stick name.
	 * @param zButton
	 *            the button that, when pressed, should have the Z-axis of this
	 *            analog stick decreased from {@code 0.0F} to {@code -1.0F}. A
	 *            value of {@code null} is permitted, and indicates that no
	 *            button corresponds to the Z-axis of this analog stick.
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}.
	 */
	public AnalogStick(String name, DeviceButton zButton) {
		super(Vector3fc.class, name);
		this.zButton = zButton;
	}

	/**
	 * Constructs a new {@code AnalogStick}.
	 * 
	 * @param name
	 *            the analog stick name.
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}.
	 */
	public AnalogStick(String name) {
		this(name, null);
	}

	@Override
	public Vector3fc zero() {
		return new Vector3f();
	}

}
