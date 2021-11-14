package com.whirvis.kibasan.seeker;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.Mouse;
import com.whirvis.kibasan.adapter.glfw.GlfwMouseAdapter;

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
