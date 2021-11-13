package org.ardenus.input.seeker;

import org.ardenus.input.InputDevice;
import org.ardenus.input.XboxController;
import org.ardenus.input.adapter.glfw.GlfwXboxAdapter;

import com.whirvex.event.EventManager;

public class GlfwXboxSeeker extends GlfwJoystickSeeker {

	public GlfwXboxSeeker(EventManager events, long ptr_glfwWindow) {
		super(XboxController.class, events, ptr_glfwWindow);
	}

	@Override
	public InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		return new XboxController(events,
				new GlfwXboxAdapter(ptr_glfwWindow, glfwJoystick));
	}

}
