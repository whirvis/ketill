package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.Keyboard;
import org.ardenus.engine.input.device.adapter.glfw.GLFWKeyboardAdapter;

public class GLFWKeyboardSeeker extends GLFWDeviceSeeker {

	private final Keyboard keyboard;
	private boolean registered;

	public GLFWKeyboardSeeker(long ptr_glfwWindow) {
		super(Keyboard.class, ptr_glfwWindow);
		this.keyboard = new Keyboard(new GLFWKeyboardAdapter(ptr_glfwWindow));
	}

	@Override
	protected void seek() throws Exception {
		if (!registered) {
			this.register(keyboard);
			this.registered = true;
		}
	}

}
