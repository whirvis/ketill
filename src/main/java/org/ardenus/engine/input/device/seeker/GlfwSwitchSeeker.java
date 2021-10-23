package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.SwitchController;
import org.ardenus.engine.input.device.adapter.glfw.GlfwSwitchAdapter;

public class GlfwSwitchSeeker extends GlfwJoystickSeeker {

	public GlfwSwitchSeeker(long ptr_glfwWindow) {
		super(SwitchController.class, ptr_glfwWindow, "Wireless Gamepad",
				"Pro Controller");
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		GlfwSwitchAdapter adapter =
				new GlfwSwitchAdapter(ptr_glfwWindow, glfwJoystick);
		return new SwitchController(adapter);
	}

}
