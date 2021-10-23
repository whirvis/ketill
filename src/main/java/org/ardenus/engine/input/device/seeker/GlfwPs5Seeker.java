package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.Ps5Controller;
import org.ardenus.engine.input.device.adapter.glfw.GlfwPs5Adapter;

public class GlfwPs5Seeker extends GlfwJoystickSeeker {

	public GlfwPs5Seeker(long ptr_glfwWindow) {
		super(Ps5Controller.class, ptr_glfwWindow);
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		return new Ps5Controller(
				new GlfwPs5Adapter(ptr_glfwWindow, glfwJoystick));
	}

}
