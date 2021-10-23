package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.Ps4Controller;
import org.ardenus.engine.input.device.adapter.glfw.GlfwPs4Adapter;

public class GlfwPs4Seeker extends GlfwJoystickSeeker {

	public GlfwPs4Seeker(long ptr_glfwWindow) {
		super(Ps4Controller.class, ptr_glfwWindow);
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		return new Ps4Controller(
				new GlfwPs4Adapter(ptr_glfwWindow, glfwJoystick));
	}

}
