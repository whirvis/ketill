package com.whirvis.kibasan.seeker;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.Ps5Controller;
import com.whirvis.kibasan.adapter.glfw.GlfwPs5Adapter;

public class GlfwPs5Seeker extends GlfwJoystickSeeker {

	public GlfwPs5Seeker(EventManager events, long ptr_glfwWindow) {
		super(Ps5Controller.class, events, ptr_glfwWindow);
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		return new Ps5Controller(events,
				new GlfwPs5Adapter(ptr_glfwWindow, glfwJoystick));
	}

}
