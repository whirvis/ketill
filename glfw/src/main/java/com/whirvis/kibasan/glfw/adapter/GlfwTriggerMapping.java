package com.whirvis.kibasan.glfw.adapter;

import com.whirvis.controller.AnalogMapping;
import com.whirvis.controller.AnalogTrigger;

public class GlfwTriggerMapping extends AnalogMapping<AnalogTrigger> {

	public final int glfwAxis;

	public GlfwTriggerMapping(AnalogTrigger analog, int glfwAxis) {
		super(analog);
		this.glfwAxis = glfwAxis;
	}

}
