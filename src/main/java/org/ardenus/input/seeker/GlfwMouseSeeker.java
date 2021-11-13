package org.ardenus.input.seeker;

import org.ardenus.input.Mouse;
import org.ardenus.input.adapter.glfw.GlfwMouseAdapter;

import com.whirvex.event.EventManager;

public class GlfwMouseSeeker extends GlfwDeviceSeeker {

	private final Mouse mouse;
	private boolean registered;

	public GlfwMouseSeeker(EventManager events, long ptr_glfwWindow) {
		super(Mouse.class, events, ptr_glfwWindow);
		this.mouse = new Mouse(events, new GlfwMouseAdapter(ptr_glfwWindow));
	}

	@Override
	protected void seek() throws Exception {
		if (!registered) {
			this.register(mouse);
			this.registered = true;
		}
	}

}
