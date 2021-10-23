package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;

/**
 * An adapter which maps input for a GLFW input device.
 *
 * @param <I>
 *            the input device type.
 * @see GlfwButtonMapping
 * @see GlfwAnalogMapping
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
