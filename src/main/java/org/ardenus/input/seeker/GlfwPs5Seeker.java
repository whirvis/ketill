package org.ardenus.input.seeker;

import org.ardenus.input.InputDevice;
import org.ardenus.input.Ps5Controller;
import org.ardenus.input.adapter.glfw.GlfwPs5Adapter;

import com.whirvex.event.EventManager;

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
