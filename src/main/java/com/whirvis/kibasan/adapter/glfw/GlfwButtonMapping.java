package com.whirvis.kibasan.adapter.glfw;

import com.whirvis.kibasan.adapter.ButtonMapping;
import com.whirvis.kibasan.feature.DeviceButton;

public class GlfwButtonMapping extends ButtonMapping {

	public final int glfwButton;

	public GlfwButtonMapping(DeviceButton button, int glfwButton) {
		super(button);
		this.glfwButton = glfwButton;
	}

}
