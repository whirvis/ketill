package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.analog.AnalogTrigger;

/**
 * An {@link AnalogTrigger} mapping for use with a {@link GLFWDeviceAdapter}.
 */
public class GLFWMappedAnalogTrigger extends GLFWMappedAnalog<AnalogTrigger> {

	public final int glfwAxis;

	/**
	 * Constructs a new {@code GLFWMappedAnalogTrigger}.
	 * 
	 * @param analog
	 *            the trigger being mapped to.
	 * @param glfwAxis
	 *            the GLFW trigger axis.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	public GLFWMappedAnalogTrigger(AnalogTrigger analog, int glfwAxis) {
		super(analog);
		this.glfwAxis = glfwAxis;
	}

}
