package org.ardenus.engine.input.device.adapter.gamecube;

import org.ardenus.engine.input.device.adapter.RumbleMapping;
import org.ardenus.engine.input.device.feature.RumbleMotor;

/**
 * A {@link RumbleMotor} mapping for use with an
 * {@link USBGameCubeControllerAdapter}.
 */
public class USBGameCubeRumbleMapping extends RumbleMapping {

	/**
	 * Constructs a new {@code USBGameCubeRumbleMapping}.
	 * 
	 * @param motor
	 *            the motor being mapped to.
	 * @throws NullPointerException
	 *             if {@code motor} is {@code null}.
	 */
	public USBGameCubeRumbleMapping(RumbleMotor motor) {
		super(motor);
	}

}
