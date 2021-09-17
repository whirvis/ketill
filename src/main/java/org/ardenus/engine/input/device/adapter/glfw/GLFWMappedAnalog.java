package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.DeviceAnalog;
import org.ardenus.engine.input.device.adapter.MappedAnalog;

/**
 * A {@link DeviceAnalog} mapping for use with a {@link GLFWDeviceAdapter}.
 * 
 * @param <A>
 *            the analog input type.
 */
public class GLFWMappedAnalog<A extends DeviceAnalog<?>>
		extends MappedAnalog<A> {

	/**
	 * Constructs a new {@code GLFWMappedAnalog}.
	 * 
	 * @param analog
	 *            the analog being mapped to.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	public GLFWMappedAnalog(A analog) {
		super(analog);
	}

}
