package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.adapter.ButtonMapping;
import org.ardenus.engine.input.device.feature.DeviceButton;

public class GLFWButtonMapping extends ButtonMapping {

	public final int glfwButton;

	public GLFWButtonMapping(DeviceButton button, int glfwButton) {
		super(button);
		this.glfwButton = glfwButton;
	}

}
