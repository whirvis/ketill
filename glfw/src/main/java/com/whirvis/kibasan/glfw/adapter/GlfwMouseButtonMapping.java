package com.whirvis.kibasan.glfw.adapter;

import com.whirvis.controller.ButtonMapping;
import com.whirvis.controller.DeviceButton;
import com.whirvis.kibasan.FeatureMapping;
import com.whirvis.kibasan.pc.MouseButton;

public class GlfwMouseButtonMapping extends FeatureMapping<MouseButton> {

	public final MouseButton button;
	public final int glfwButton;

	public GlfwMouseButtonMapping(MouseButton button, int glfwButton) {
		super(button);
		this.button = button;
		this.glfwButton = glfwButton;
	}

}
