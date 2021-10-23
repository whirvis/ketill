package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.feature.AnalogTrigger;

public class GlfwTriggerMapping extends GlfwAnalogMapping<AnalogTrigger> {

	public final int glfwAxis;

	public GlfwTriggerMapping(AnalogTrigger analog, int glfwAxis) {
		super(analog);
		this.glfwAxis = glfwAxis;
	}

}
