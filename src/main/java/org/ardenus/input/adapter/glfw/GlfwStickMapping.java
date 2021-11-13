package org.ardenus.input.adapter.glfw;

import org.ardenus.input.adapter.AnalogMapping;
import org.ardenus.input.feature.AnalogStick;

public class GlfwStickMapping extends AnalogMapping<AnalogStick> {

	public final int glfwAxisX;
	public final int glfwAxisY;

	public GlfwStickMapping(AnalogStick analog, int glfwAxisX,
			int glfwAxisY) {
		super(analog);
		this.glfwAxisX = glfwAxisX;
		this.glfwAxisY = glfwAxisY;
	}

}
