package com.whirvis.kibasan.glfw.adapter;

import com.whirvis.controller.AnalogMapping;
import com.whirvis.controller.AnalogStick;

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
