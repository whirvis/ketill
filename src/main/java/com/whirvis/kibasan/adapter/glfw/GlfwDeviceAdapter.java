package com.whirvis.kibasan.adapter.glfw;

import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.adapter.DeviceAdapter;

/**
 * An adapter which maps input for a GLFW input device.
 *
 * @param <I>
 *            the input device type.
 * @see GlfwButtonMapping
 */
public abstract class GlfwDeviceAdapter<I extends InputDevice>
		extends DeviceAdapter<I> {

	protected final long ptr_glfwWindow;

	/**
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 */
	public GlfwDeviceAdapter(long ptr_glfwWindow) {
		this.ptr_glfwWindow = ptr_glfwWindow;
	}

}
