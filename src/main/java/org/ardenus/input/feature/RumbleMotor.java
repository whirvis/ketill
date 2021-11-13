package org.ardenus.input.feature;

import java.util.Objects;

public class RumbleMotor implements DeviceFeature<Vibration1f> {

	private final String id;

	/**
	 * @param id
	 *            the rumble motor ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public RumbleMotor(String id) {
		this.id = Objects.requireNonNull(id, "id");
	}

	@Override
	public String id() {
		return this.id;
	}

	@Override
	public Vibration1f initial() {
		return new Vibration1f();
	}

}
