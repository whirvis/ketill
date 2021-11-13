package org.ardenus.input.adapter.glfw;

import org.ardenus.input.adapter.KeyMapping;
import org.ardenus.input.feature.KeyboardKey;

public class GlfwKeyMapping extends KeyMapping {

	public final int glfwKey;

	public GlfwKeyMapping(KeyboardKey key, int glfwKey) {
		super(key);
		this.glfwKey = glfwKey;
	}

}
