package org.ardenus.input.feature;

public class Vibration1f {

	public float force;

	/**
	 * @param force
	 *            the initial vibration force.
	 */
	public Vibration1f(float force) {
		this.force = force;
	}

	/**
	 * Constructs a new {@code Vibration1f} with a force of {@code 0.0F}.
	 */
	public Vibration1f() {
		this(0.0F);
	}

}
