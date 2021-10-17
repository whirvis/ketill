package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.Mouse;
import org.ardenus.engine.input.device.adapter.glfw.GLFWMouseAdapter;

public class GLFWMouseSeeker extends GLFWDeviceSeeker {

	private final Mouse mouse;
	private boolean registered;

	public GLFWMouseSeeker(long ptr_glfwWindow) {
		super(Mouse.class, ptr_glfwWindow);
		this.mouse = new Mouse(new GLFWMouseAdapter(ptr_glfwWindow));
	}

	@Override
	protected void seek() throws Exception {
		if (!registered) {
			this.register(mouse);
			this.registered = true;
		}
	}

}
