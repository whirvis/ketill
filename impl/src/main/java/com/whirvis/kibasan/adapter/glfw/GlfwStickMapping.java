package com.whirvis.kibasan.adapter.glfw;

import com.whirvis.controller.AnalogStick;
import com.whirvis.kibasan.adapter.AnalogMapping;

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
