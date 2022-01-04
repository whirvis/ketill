package com.whirvis.kibasan.seeker;

import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.XboxController;
import com.whirvis.kibasan.adapter.glfw.GlfwXboxAdapter;

public class GlfwXboxSeeker extends GlfwJoystickSeeker {

	public GlfwXboxSeeker(long ptr_glfwWindow) {
		super(XboxController.class, ptr_glfwWindow);
	}

	@Override
	public InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		return new XboxController(
				new GlfwXboxAdapter(ptr_glfwWindow, glfwJoystick));
	}

}
