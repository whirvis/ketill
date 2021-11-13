package org.ardenus.input.seeker;

import org.ardenus.input.Keyboard;
import org.ardenus.input.adapter.glfw.GlfwKeyboardAdapter;

import com.whirvex.event.EventManager;

public class GlfwKeyboardSeeker extends GlfwDeviceSeeker {

	private final Keyboard keyboard;
	private boolean registered;

	public GlfwKeyboardSeeker(EventManager events, long ptr_glfwWindow) {
		super(Keyboard.class, events, ptr_glfwWindow);
		this.keyboard =
				new Keyboard(events, new GlfwKeyboardAdapter(ptr_glfwWindow));
	}

	@Override
	protected void seek() throws Exception {
		if (!registered) {
			this.register(keyboard);
			this.registered = true;
		}
	}

}
