package org.ardenus.engine.input.device.feature;

import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Represents an analog stick on an input device.
 */
public class AnalogStick extends DeviceAnalog<Vector3fc> {

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
