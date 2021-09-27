package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.adapter.MappedButton;
import org.ardenus.engine.input.device.button.DeviceButton;

/**
 * A {@link DeviceButton} mapping for use with a {@link GLFWDeviceAdapter}.
 */
public class GLFWMappedButton extends MappedButton {

	public final int glfwButton;

	/**
	 * Constructs a new {@code GLFWMappedButton}.
	 * 
	 * @param button
	 *            the button being mapped to.
	 * @param glfwButton
	 *            the GLFW button ID.
	 * @throws NullPointerException
	 *             if {@code button} is {@code null}.
	 */
	public GLFWMappedButton(DeviceButton button, int glfwButton) {
		super(button);
		this.glfwButton = glfwButton;
	}

}
