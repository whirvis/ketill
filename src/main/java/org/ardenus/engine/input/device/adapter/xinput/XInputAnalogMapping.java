package org.ardenus.engine.input.device.adapter.xinput;

import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.feature.DeviceAnalog;

/**
 * A {@link DeviceAnalog} mapping for use with an {@link XInputDeviceAdapter}.
 * 
 * @param <A>
 *            the analog input type.
 */
public class XInputAnalogMapping<A extends DeviceAnalog<?>>
		extends AnalogMapping<A> {

	/**
	 * Constructs a new {@code XInputMappedAnalog}.
	 * 
	 * @param analog
	 *            the analog being mapped to.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	public XInputAnalogMapping(A analog) {
		super(analog);
	}

}
