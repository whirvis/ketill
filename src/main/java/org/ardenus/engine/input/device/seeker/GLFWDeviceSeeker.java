package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.InputDevice;

/**
 * A device seeker for devices using GLFW.
 */
public abstract class GLFWDeviceSeeker extends DeviceSeeker {

	protected final long ptr_glfwWindow;

	/**
	 * Constructs a new {@code GLFWDeviceSeeker}.
	 * 
	 * @param type
	 *            the input device type.
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}.
	 */
	public GLFWDeviceSeeker(Class<? extends InputDevice> type,
			long ptr_glfwWindow) {
		super(type);
		this.ptr_glfwWindow = ptr_glfwWindow;
	}

}
