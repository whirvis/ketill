package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.XboxController;
import org.ardenus.engine.input.device.adapter.glfw.GlfwXboxAdapter;

public class GlfwXboxSeeker extends GlfwJoystickSeeker {

	public GlfwXboxSeeker(long ptr_glfwWindow) {
		super(XboxController.class, ptr_glfwWindow,
				"Afterglow Gamepad for Xbox 360", "Microsoft X-Box 360 pad",
				"Wireless Xbox 360 Controller", "Wireless Xbox Controller",
				"Redgear", "Xbox 360 Controller", "Xbox Controller");
	}

	@Override
	public InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		GlfwXboxAdapter adapter =
				new GlfwXboxAdapter(ptr_glfwWindow, glfwJoystick);
		return new XboxController(adapter);
	}

}
