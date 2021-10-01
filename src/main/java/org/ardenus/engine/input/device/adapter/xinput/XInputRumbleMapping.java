package org.ardenus.engine.input.device.adapter.xinput;

import org.ardenus.engine.input.device.adapter.mapping.RumbleMapping;
import org.ardenus.engine.input.device.rumble.RumbleMotor;

/**
 * A {@link RumbleMotor} mapping for use with an {@link XInputDeviceAdapter}.
 */
public class XInputRumbleMapping extends RumbleMapping {

	/**
	 * Constructs a new {@code XInputRumbleMapping}.
	 * 
	 * @param motor
	 *            the motor being mapped to.
	 * @throws NullPointerException
	 *             if {@code motor} is {@code null}.
	 */
	public XInputRumbleMapping(RumbleMotor motor) {
		super(motor);
	}

}
