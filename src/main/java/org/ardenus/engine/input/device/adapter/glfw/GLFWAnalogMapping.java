package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.feature.DeviceAnalog;

/**
 * A {@link DeviceAnalog} mapping for use with a {@link GLFWDeviceAdapter}.
 * 
 * @param <A>
 *            the analog input type.
 */
public class GLFWAnalogMapping<A extends DeviceAnalog<?>>
		extends AnalogMapping<A> {

	/**
	 * Constructs a new {@code GLFWMappedAnalog}.
	 * 
	 * @param analog
	 *            the analog being mapped to.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	public GLFWAnalogMapping(A analog) {
		super(analog);
	}

}
