package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.SwitchProController;
import org.ardenus.engine.input.device.adapter.glfw.GlfwSwitchProAdapter;

public class GlfwSwitchProSeeker extends GlfwJoystickSeeker {

	public GlfwSwitchProSeeker(long ptr_glfwWindow) {
		super(SwitchProController.class, ptr_glfwWindow, "Wireless Gamepad",
				"Pro Controller");
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		GlfwSwitchProAdapter adapter =
				new GlfwSwitchProAdapter(ptr_glfwWindow, glfwJoystick);
		return new SwitchProController(adapter);
	}

}
