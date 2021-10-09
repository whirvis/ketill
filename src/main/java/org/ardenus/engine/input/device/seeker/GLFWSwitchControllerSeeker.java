package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.SwitchController;
import org.ardenus.engine.input.device.adapter.glfw.GLFWSwitchControllerAdapter;

/**
 * A device seeker for {@code SwitchController} devices using GLFW.
 */
public class GLFWSwitchControllerSeeker extends GLFWDeviceSeeker {

	/**
	 * Constructs a new {@code GLFWSwitchControllerSeeker}.
	 * 
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 */
	public GLFWSwitchControllerSeeker(long ptr_glfwWindow) {
		super(SwitchController.class, ptr_glfwWindow, "Wireless Gamepad",
				"Pro Controller");
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		GLFWSwitchControllerAdapter adapter =
				new GLFWSwitchControllerAdapter(ptr_glfwWindow, glfwJoystick);
		return new SwitchController(adapter);
	}

}
