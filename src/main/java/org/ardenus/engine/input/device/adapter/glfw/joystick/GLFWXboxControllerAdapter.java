package org.ardenus.engine.input.device.adapter.glfw.joystick;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.adapter.glfw.GLFWButtonMapping;
import org.ardenus.engine.input.device.adapter.glfw.analog.GLFWAnalogMapping;
import org.ardenus.engine.input.device.adapter.glfw.analog.GLFWAnalogStickMapping;
import org.ardenus.engine.input.device.adapter.glfw.analog.GLFWAnalogTriggerMapping;
import org.ardenus.engine.input.device.adapter.mapping.AdapterMapping;
import org.ardenus.engine.input.device.analog.Trigger1f;
import org.ardenus.engine.input.device.controller.XboxController;
import org.joml.Vector3f;

/**
 * A GLFW joystick adapter for an {@link XboxController}.
 * 
 * @see GLFWJoystickAdapter
 */
public class GLFWXboxControllerAdapter
		extends GLFWJoystickAdapter<XboxController> {

	@AdapterMapping
	private static final GLFWAnalogMapping<?> MA_LS =
			new GLFWAnalogStickMapping(XboxController.STICK_L, 0, 1),
			MA_RS = new GLFWAnalogStickMapping(XboxController.STICK_R, 2, 3),
			MA_LT = new GLFWAnalogTriggerMapping(XboxController.TRIGGER_L, 4),
			MA_RT = new GLFWAnalogTriggerMapping(XboxController.TRIGGER_R, 5);

	@AdapterMapping
	private static final GLFWButtonMapping MB_A =
			new GLFWButtonMapping(XboxController.BUTTON_A, 0),
			MB_B = new GLFWButtonMapping(XboxController.BUTTON_B, 1),
			MB_X = new GLFWButtonMapping(XboxController.BUTTON_X, 2),
			MB_Y = new GLFWButtonMapping(XboxController.BUTTON_Y, 3),
			MB_LB = new GLFWButtonMapping(XboxController.BUTTON_LB, 4),
			MB_RB = new GLFWButtonMapping(XboxController.BUTTON_RB, 5),
			MB_MENU = new GLFWButtonMapping(XboxController.BUTTON_MENU, 6),
			MB_PAUSE = new GLFWButtonMapping(XboxController.BUTTON_PAUSE, 7),
			MB_LS = new GLFWButtonMapping(XboxController.BUTTON_LS, 8),
			MB_RS = new GLFWButtonMapping(XboxController.BUTTON_RS, 9),
			MB_UP = new GLFWButtonMapping(XboxController.BUTTON_UP, 10),
			MB_RIGHT = new GLFWButtonMapping(XboxController.BUTTON_RIGHT, 11),
			MB_DOWN = new GLFWButtonMapping(XboxController.BUTTON_DOWN, 12),
			MB_LEFT = new GLFWButtonMapping(XboxController.BUTTON_LEFT, 13);

	/**
	 * Constructs a new {@code GLFWXboxControllerAdapter}.
	 * 
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @param glfwJoystick
	 *            the GLFW joystick ID.
	 * @throws InputException
	 *             if an input error occurs.
	 */
	public GLFWXboxControllerAdapter(long ptr_glfwWindow, int glfwJoystick) {
		super(ptr_glfwWindow, glfwJoystick);
	}

	@Override
	public void updateAnalogStick(GLFWAnalogStickMapping mapping,
			Vector3f stick) {
		super.updateAnalogStick(mapping, stick);
		if (mapping == MA_LS || mapping == MA_RS) {
			stick.y *= -1.0F;
		}
	}

	@Override
	public void updateAnalogTrigger(GLFWAnalogTriggerMapping mapping,
			Trigger1f trigger) {
		super.updateAnalogTrigger(mapping, trigger);
		if (mapping == MA_LT || mapping == MA_RT) {
			trigger.force += 1.0F;
			trigger.force /= 2.0F;
		}
	}

}
