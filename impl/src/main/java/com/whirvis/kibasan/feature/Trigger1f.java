package com.whirvis.kibasan.feature;

public class Trigger1f implements Trigger1fc {

	public float force;

	/**
	 * @param force
	 *            the initial trigger force.
	 */
	public Trigger1f(float force) {
		this.force = force;
	}

	/**
	 * Constructs a new {@code Trigger1f} with a force of {@code 0.0F}.
	 */
	public Trigger1f() {
		this(0.0F);
	}

	@Override
	public float force() {
		return this.force;
	}

}
