package com.whirvis.kibasan.adapter.glfw;

import com.whirvis.controller.DeviceButton;
import com.whirvis.kibasan.adapter.ButtonMapping;

public class GlfwButtonMapping extends ButtonMapping {

	public final int glfwButton;

	public GlfwButtonMapping(DeviceButton button, int glfwButton) {
		super(button);
		this.glfwButton = glfwButton;
	}

}
