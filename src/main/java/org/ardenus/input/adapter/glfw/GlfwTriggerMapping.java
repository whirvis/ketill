package org.ardenus.input.adapter.glfw;

import org.ardenus.input.adapter.AnalogMapping;
import org.ardenus.input.feature.AnalogTrigger;

public class GlfwTriggerMapping extends AnalogMapping<AnalogTrigger> {

	public final int glfwAxis;

	public GlfwTriggerMapping(AnalogTrigger analog, int glfwAxis) {
		super(analog);
		this.glfwAxis = glfwAxis;
	}

}
