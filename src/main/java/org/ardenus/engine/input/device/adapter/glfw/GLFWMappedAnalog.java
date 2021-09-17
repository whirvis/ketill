package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.DeviceAnalog;
import org.ardenus.engine.input.device.adapter.MappedAnalog;

/**
 * A {@link DeviceAnalog} mapping for use with a {@link GLFWDeviceAdapter}.
 */
public class GLFWMappedAnalog extends MappedAnalog {

	public final int glfwAxis;

	/**
	 * Constructs a new {@code GLFWMappedAnalog}.
	 * 
	 * @param analog
	 *            the analog being mapped to.
	 * @param glfwAxis
	 *            the GLFW axis ID.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	public GLFWMappedAnalog(DeviceAnalog<?> analog, int glfwAxis) {
		super(analog);
		this.glfwAxis = glfwAxis;
	}

}
