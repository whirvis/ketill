package org.ardenus.engine.input.device.rumble;

import java.util.Objects;

import org.ardenus.engine.input.device.DeviceFeature;

/**
 * Represents a rumble motor on an input device.
 */
public class RumbleMotor implements DeviceFeature<Vibration1f> {

	private final String name;

	/**
	 * Constructs a new {@code RumbleMotor}.
	 * 
	 * @param name
	 *            the rumble motor name.
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}.
	 */
	public RumbleMotor(String name) {
		Objects.requireNonNull(name, "name");
		this.name = name + " Rumble";
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public Vibration1f initial() {
		return new Vibration1f();
	}

}
