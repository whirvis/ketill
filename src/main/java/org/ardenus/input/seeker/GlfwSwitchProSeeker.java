package org.ardenus.input.seeker;

import org.ardenus.input.InputDevice;
import org.ardenus.input.SwitchProController;
import org.ardenus.input.adapter.glfw.GlfwSwitchProAdapter;

import com.whirvex.event.EventManager;

public class GlfwSwitchProSeeker extends GlfwJoystickSeeker {

	public GlfwSwitchProSeeker(EventManager events, long ptr_glfwWindow) {
		super(SwitchProController.class, events, ptr_glfwWindow);
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		return new SwitchProController(events,
				new GlfwSwitchProAdapter(ptr_glfwWindow, glfwJoystick));
	}

}
