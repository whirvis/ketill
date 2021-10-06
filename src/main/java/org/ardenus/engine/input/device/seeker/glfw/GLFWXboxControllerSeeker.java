package org.ardenus.engine.input.device.seeker.glfw;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.adapter.glfw.joystick.GLFWXboxControllerAdapter;
import org.ardenus.engine.input.device.controller.XboxController;

/**
 * A device seeker for {@code XboxController} devices using GLFW.
 */
public class GLFWXboxControllerSeeker extends GLFWDeviceSeeker {

	/**
	 * Constructs a new {@code GLFWXboxControllerSeeker}.
	 * 
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 */
	public GLFWXboxControllerSeeker(long ptr_glfwWindow) {
		super(XboxController.class, ptr_glfwWindow,
				"Afterglow Gamepad for Xbox 360", "Microsoft X-Box 360 pad",
				"Wireless Xbox 360 Controller", "Wireless Xbox Controller",
				"Redgear", "Xbox 360 Controller", "Xbox Controller");
	}

	@Override
	public InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		GLFWXboxControllerAdapter adapter =
				new GLFWXboxControllerAdapter(ptr_glfwWindow, glfwJoystick);
		return new XboxController(adapter);
	}

}
