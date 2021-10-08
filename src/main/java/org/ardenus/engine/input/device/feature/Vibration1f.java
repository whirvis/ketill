package org.ardenus.engine.input.device.feature;

/**
 * Contains the definition of a vibration comprising of 1 {@code float}.
 */
public class Vibration1f {

	public float force;

	/**
	 * Constructs a new {@code Vibration1f}.
	 * 
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
