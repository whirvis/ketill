package org.ardenus.input.adapter.glfw;

import org.ardenus.input.adapter.ButtonMapping;
import org.ardenus.input.feature.DeviceButton;

public class GlfwButtonMapping extends ButtonMapping {

	public final int glfwButton;

	public GlfwButtonMapping(DeviceButton button, int glfwButton) {
		super(button);
		this.glfwButton = glfwButton;
	}

}
