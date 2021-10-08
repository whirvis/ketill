package org.ardenus.engine.input.device.feature;

import java.util.Objects;

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
