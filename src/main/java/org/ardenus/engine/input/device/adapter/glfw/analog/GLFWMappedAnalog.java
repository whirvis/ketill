package org.ardenus.engine.input.device.adapter.glfw.analog;

import org.ardenus.engine.input.device.adapter.MappedAnalog;
import org.ardenus.engine.input.device.adapter.glfw.GLFWDeviceAdapter;
import org.ardenus.engine.input.device.analog.DeviceAnalog;

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
