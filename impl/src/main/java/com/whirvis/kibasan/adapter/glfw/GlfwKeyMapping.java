package com.whirvis.kibasan.adapter.glfw;

import com.whirvis.kibasan.adapter.KeyMapping;
import com.whirvis.kibasan.feature.KeyboardKey;

public class GlfwKeyMapping extends KeyMapping {

	public final int glfwKey;

	public GlfwKeyMapping(KeyboardKey key, int glfwKey) {
		super(key);
		this.glfwKey = glfwKey;
	}

}
