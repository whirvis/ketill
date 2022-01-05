package com.whirvis.kibasan.glfw.seeker;

import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.glfw.adapter.GlfwPs5Adapter;
import com.whirvis.kibasan.psx.Ps5Controller;

public class GlfwPs5Seeker extends GlfwJoystickSeeker {

	public GlfwPs5Seeker(long ptr_glfwWindow) {
		super(Ps5Controller.class, "ps5", ptr_glfwWindow);
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		return new Ps5Controller(
				new GlfwPs5Adapter(ptr_glfwWindow, glfwJoystick));
	}

}
