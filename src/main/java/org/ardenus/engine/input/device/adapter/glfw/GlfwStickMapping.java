package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.feature.AnalogStick;

public class GlfwStickMapping extends GlfwAnalogMapping<AnalogStick> {

	public final int glfwAxisX;
	public final int glfwAxisY;

	public GlfwStickMapping(AnalogStick analog, int glfwAxisX,
			int glfwAxisY) {
		super(analog);
		this.glfwAxisX = glfwAxisX;
		this.glfwAxisY = glfwAxisY;
	}

}
