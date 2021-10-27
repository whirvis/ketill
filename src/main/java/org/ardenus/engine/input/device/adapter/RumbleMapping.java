package org.ardenus.engine.input.device.adapter;

import org.ardenus.engine.input.device.feature.RumbleMotor;

/**
 * A {@link RumbleMotor} mapping for use with a {@link DeviceAdapter}.
 * <p>
 * On their own, a rumble mapping cannot provide a meaningful mapping for an
 * rumble feature. It must be extended by a class which provides information
 * meaningful to the context of a relevant device adapter. This can be as simple
 * as providing an extra field for a motor ID. An example of this would be:
 * 
 * <pre>
 * public class Ds4RumbleMapping extends RumbleMapping {
 * 
 * 	public final int byteOffset;
 * 
 * 	public Ds4RumbleMapping(RumbleMotor motor, int byteOffset) {
 * 		super(motor);
 * 		this.byteOffset = byteOffset;
 * 	}
 * 
 * }
 * </pre>
 * 
 * @see ButtonMapping
 * @see AnalogMapping
 */
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
