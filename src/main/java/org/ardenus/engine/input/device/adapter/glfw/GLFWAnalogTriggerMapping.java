package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.feature.AnalogTrigger;

public class GLFWAnalogTriggerMapping extends GLFWAnalogMapping<AnalogTrigger> {

	public final int glfwAxis;

	public GLFWAnalogTriggerMapping(AnalogTrigger analog, int glfwAxis) {
		super(analog);
		this.glfwAxis = glfwAxis;
	}

}
