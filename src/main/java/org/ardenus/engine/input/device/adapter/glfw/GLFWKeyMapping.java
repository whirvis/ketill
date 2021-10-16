package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.adapter.KeyMapping;
import org.ardenus.engine.input.device.feature.KeyboardKey;

public class GLFWKeyMapping extends KeyMapping {

	public final int glfwKey;

	public GLFWKeyMapping(KeyboardKey key, int glfwKey) {
		super(key);
		this.glfwKey = glfwKey;
	}

}
