package org.ardenus.engine.input.device.adapter.glfw.analog;

import org.ardenus.engine.input.device.adapter.glfw.GLFWDeviceAdapter;
import org.ardenus.engine.input.device.feature.AnalogTrigger;

/**
 * An {@link AnalogTrigger} mapping for use with a {@link GLFWDeviceAdapter}.
 */
public class GLFWAnalogTriggerMapping extends GLFWAnalogMapping<AnalogTrigger> {

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
	public GLFWAnalogTriggerMapping(AnalogTrigger analog, int glfwAxis) {
		super(analog);
		this.glfwAxis = glfwAxis;
	}

}
