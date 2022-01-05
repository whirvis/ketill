package com.whirvis.kibasan.glfw.adapter;

import com.whirvis.controller.ButtonMapping;
import com.whirvis.controller.DeviceButton;

public class GlfwButtonMapping extends ButtonMapping {

	public final int glfwButton;

	public GlfwButtonMapping(DeviceButton button, int glfwButton) {
		super(button);
		this.glfwButton = glfwButton;
	}

}
