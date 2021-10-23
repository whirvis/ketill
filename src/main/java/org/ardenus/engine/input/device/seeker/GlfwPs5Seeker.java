package org.ardenus.engine.input.device.seeker;

import java.util.HashSet;
import java.util.Set;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.Ps5Controller;
import org.ardenus.engine.input.device.adapter.glfw.GlfwPs5Adapter;

public class GlfwPs5Seeker extends GlfwJoystickSeeker {

	/*
	 * TODO: Use these GUIDs in the proper context of a PS5 adapter.
	 * 
	 * TODO: Quick hack to check if the controller is a PS5 controller,
	 * according to the SDL bindings. This is not good. This is terrible!
	 * Horrible! I hate this! A better solution to this should be used in the
	 * future. I'm only leaving this here now, as I have a presentation on this
	 * in a few days. I will fix this as soon as I have the time.
	 * 
	 * SDL bindings: https://github.com/gabomdq/SDL_GameControllerDB/
	 */
	private static final Set<String> PS5_GUIDS = new HashSet<>();
	static {
		PS5_GUIDS.add("030000004c050000e60c000000000000");
		PS5_GUIDS.add("050000004c050000e60c000000010000");
		PS5_GUIDS.add("030000004c050000e60c000011010000");
		PS5_GUIDS.add("050000004c050000e60c000000010000");
		PS5_GUIDS.add("050000004c050000e60c0000fffe3f00");
		PS5_GUIDS.add("050000004c050000e60c0000df870000");
		PS5_GUIDS.add("050000004c050000e60c0000ff870000");
	}

	public GlfwPs5Seeker(long ptr_glfwWindow) {
		super(Ps5Controller.class, ptr_glfwWindow);
		
		/* TODO: remove this once ready */
		throw new UnsupportedOperationException();
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		return new Ps5Controller(
				new GlfwPs5Adapter(ptr_glfwWindow, glfwJoystick));
	}

}
