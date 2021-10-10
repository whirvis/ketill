package org.ardenus.engine.input.device.adapter.gamecube;

import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.feature.DeviceAnalog;

/**
 * A {@link DeviceAnalog} mapping for use with a
 * {@link USBGameCubeControllerAdapter}.
 * 
 * @param <A>
 *            the analog input type.
 */
public class USBGameCubeAnalogMapping<A extends DeviceAnalog<?>>
		extends AnalogMapping<A> {

	/**
	 * Constructs a new {@code GamecubeAnalogMapping}.
	 * 
	 * @param analog
	 *            the analog being mapped to.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	public USBGameCubeAnalogMapping(A analog) {
		super(analog);
	}

}
