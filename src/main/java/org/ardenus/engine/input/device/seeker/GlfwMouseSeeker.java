package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.Mouse;
import org.ardenus.engine.input.device.adapter.glfw.GlfwMouseAdapter;

public class GlfwMouseSeeker extends GlfwDeviceSeeker {

	private final Mouse mouse;
	private boolean registered;

	public GlfwMouseSeeker(long ptr_glfwWindow) {
		super(Mouse.class, ptr_glfwWindow);
		this.mouse = new Mouse(new GlfwMouseAdapter(ptr_glfwWindow));
	}

	@Override
	protected void seek() throws Exception {
		if (!registered) {
			this.register(mouse);
			this.registered = true;
		}
	}

}
