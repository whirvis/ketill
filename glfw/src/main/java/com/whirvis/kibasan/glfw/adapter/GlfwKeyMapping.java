package com.whirvis.kibasan.glfw.adapter;

import com.whirvis.kibasan.pc.KeyMapping;
import com.whirvis.kibasan.pc.KeyboardKey;

public class GlfwKeyMapping extends KeyMapping {

	public final int glfwKey;

	public GlfwKeyMapping(KeyboardKey key, int glfwKey) {
		super(key);
		this.glfwKey = glfwKey;
	}

}
