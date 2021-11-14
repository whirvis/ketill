package com.whirvis.kibasan.adapter.glfw;

import com.whirvis.kibasan.adapter.AnalogMapping;
import com.whirvis.kibasan.feature.AnalogTrigger;

public class GlfwTriggerMapping extends AnalogMapping<AnalogTrigger> {

	public final int glfwAxis;

	public GlfwTriggerMapping(AnalogTrigger analog, int glfwAxis) {
		super(analog);
		this.glfwAxis = glfwAxis;
	}

}
