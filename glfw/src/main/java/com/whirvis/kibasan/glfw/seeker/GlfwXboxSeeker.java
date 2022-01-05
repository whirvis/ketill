package com.whirvis.kibasan.glfw.seeker;

import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.glfw.adapter.GlfwXboxAdapter;
import com.whirvis.kibasan.xbox.XboxController;

public class GlfwXboxSeeker extends GlfwJoystickSeeker {

	public GlfwXboxSeeker(long ptr_glfwWindow) {
		super(XboxController.class, "xbox", ptr_glfwWindow);
	}

	@Override
	public InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		return new XboxController(
				new GlfwXboxAdapter(ptr_glfwWindow, glfwJoystick));
	}

}
