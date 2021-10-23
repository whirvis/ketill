package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.adapter.ButtonMapping;
import org.ardenus.engine.input.device.feature.DeviceButton;

public class GlfwButtonMapping extends ButtonMapping {

	public final int glfwButton;

	public GlfwButtonMapping(DeviceButton button, int glfwButton) {
		super(button);
		this.glfwButton = glfwButton;
	}

}
