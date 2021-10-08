package org.ardenus.engine.input.device.adapter.mapping;

import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.RumbleMotor;

/**
 * A {@link RumbleMotor} mapping for use with a {@link DeviceAdapter}.
 */
public abstract class RumbleMapping extends FeatureMapping<RumbleMotor> {

	/**
	 * Constructs a new {@code RumbleMapping}.
	 * 
	 * @param motor
	 *            the motor being mapped to.
	 * @throws NullPointerException
	 *             if {@code motor} is {@code null}.
	 */
	public RumbleMapping(RumbleMotor motor) {
		super(motor);
	}

}
