package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.PlayStationController;
import org.ardenus.engine.input.device.adapter.glfw.GLFWPlayStationControllerAdapter;

/**
 * A device seeker for {@code PlayStationController} devices using GLFW.
 */
public class GLFWPlayStationControllerSeeker extends GLFWDeviceSeeker {

	/**
	 * Constructs a new {@code GLFWPlayStationControllerSeeker}.
	 * 
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 */
	public GLFWPlayStationControllerSeeker(long ptr_glfwWindow) {
		super(PlayStationController.class, ptr_glfwWindow,
				"Wireless Controller", "USB Joystick",
				"SPEEDLINK STRIKE Gamepad");
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		GLFWPlayStationControllerAdapter adapter =
				new GLFWPlayStationControllerAdapter(ptr_glfwWindow,
						glfwJoystick);
		return new PlayStationController(adapter);
	}

}
