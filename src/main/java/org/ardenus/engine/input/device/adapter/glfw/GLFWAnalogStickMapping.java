package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.feature.AnalogStick;

public class GLFWAnalogStickMapping extends GLFWAnalogMapping<AnalogStick> {

	public final int glfwAxisX;
	public final int glfwAxisY;

	public GLFWAnalogStickMapping(AnalogStick analog, int glfwAxisX,
			int glfwAxisY) {
		super(analog);
		this.glfwAxisX = glfwAxisX;
		this.glfwAxisY = glfwAxisY;
	}

}
