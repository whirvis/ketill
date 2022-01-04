package com.whirvis.kibasan.seeker;

import com.whirvis.kibasan.Keyboard;
import com.whirvis.kibasan.adapter.glfw.GlfwKeyboardAdapter;

public class GlfwKeyboardSeeker extends GlfwDeviceSeeker {

	private final Keyboard keyboard;
	private boolean registered;

	public GlfwKeyboardSeeker(long ptr_glfwWindow) {
		super(Keyboard.class, ptr_glfwWindow);
		this.keyboard =
				new Keyboard(new GlfwKeyboardAdapter(ptr_glfwWindow));
	}

	@Override
	protected void seek() throws Exception {
		if (!registered) {
			this.register(keyboard);
			this.registered = true;
		}
	}

}
