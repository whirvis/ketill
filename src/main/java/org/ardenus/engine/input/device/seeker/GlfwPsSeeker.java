package org.ardenus.engine.input.device.seeker;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashSet;
import java.util.Set;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.PsController;
import org.ardenus.engine.input.device.adapter.glfw.GlfwPsAdapter;

public class GlfwPsSeeker extends GlfwJoystickSeeker {

	/*
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

	public GlfwPsSeeker(long ptr_glfwWindow) {
		super(PsController.class, ptr_glfwWindow,
				"Wireless Controller", "USB Joystick",
				"SPEEDLINK STRIKE Gamepad");
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		String guid = glfwGetJoystickGUID(glfwJoystick);
		GlfwPsAdapter adapter =
				new GlfwPsAdapter(ptr_glfwWindow,
						glfwJoystick, PS5_GUIDS.contains(guid));
		return new PsController(adapter);
	}

}
