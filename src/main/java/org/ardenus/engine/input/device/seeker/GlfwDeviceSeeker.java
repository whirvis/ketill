package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.InputDevice;

public abstract class GlfwDeviceSeeker extends DeviceSeeker {

	protected final long ptr_glfwWindow;

	/**
	 * @param type
	 *            the input device type.
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}.
	 */
	public GlfwDeviceSeeker(Class<? extends InputDevice> type,
			long ptr_glfwWindow) {
		super(type);
		this.ptr_glfwWindow = ptr_glfwWindow;
	}

}
