package com.whirvis.kibasan.seeker;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.InputDevice;

public abstract class GlfwDeviceSeeker extends DeviceSeeker {

	protected final long ptr_glfwWindow;

	/**
	 * @param type
	 *            the input device type.
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}.
	 */
	public GlfwDeviceSeeker(Class<? extends InputDevice> type,
			EventManager events, long ptr_glfwWindow) {
		super(type, events);
		this.ptr_glfwWindow = ptr_glfwWindow;
	}

}
