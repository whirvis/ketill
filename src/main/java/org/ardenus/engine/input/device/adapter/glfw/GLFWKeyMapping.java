package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.adapter.KeyMapping;
import org.ardenus.engine.input.device.feature.KeyboardKey;

/**
 * A {@link KeyboardKey} mapping for use with a {@link GLFWKeyboardAdapter}.
 */
public class GLFWKeyMapping extends KeyMapping {

	public final int glfwKey;
	
	/**
	 * Constructs a new {@code GLFWKeyMapping}.
	 * 
	 * @param key
	 *            the key being mapped to.
	 * @param glfwKey
	 *            the GLFW key ID.
	 * @throws NullPointerException
	 *             if {@code key} is {@code null}.
	 */
	public GLFWKeyMapping(KeyboardKey key, int glfwKey) {
		super(key);
		this.glfwKey = glfwKey;
	}

}
