package org.ardenus.engine.input.device.adapter;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.adapter.mapping.AnalogMapping;
import org.ardenus.engine.input.device.adapter.mapping.ButtonMapping;

/**
 * An adapter which maps input for a GLFW input device.
 *
 * @param <I>
 *            the input device type.
 * @see GLFWMappedAnalog
 * @see GLFWMappedButton
 */
public abstract class GLFWDeviceAdapter<I extends InputDevice>
		extends DeviceAdapter<I> {

	protected final long ptr_glfwWindow;

	/**
	 * Constructs a new {@code GLFWDeviceAdapter}.
	 * 
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @throws InputException
	 *             if an input error occurs.
	 * @see #map(AnalogMapping)
	 * @see #map(ButtonMapping)
	 */
	public GLFWDeviceAdapter(long ptr_glfwWindow) {
		this.ptr_glfwWindow = ptr_glfwWindow;
	}

}
