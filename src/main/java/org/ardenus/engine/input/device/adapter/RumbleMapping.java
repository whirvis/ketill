package org.ardenus.engine.input.device.adapter;

import org.ardenus.engine.input.device.feature.RumbleMotor;

public abstract class RumbleMapping extends FeatureMapping<RumbleMotor> {

	/**
	 * @param motor
	 *            the motor being mapped to.
	 * @throws NullPointerException
	 *             if {@code motor} is {@code null}.
	 */
	public RumbleMapping(RumbleMotor motor) {
		super(motor);
	}

}
