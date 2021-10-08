package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.adapter.AdapterMapping;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.ardenus.engine.input.device.controller.XboxController;
import org.ardenus.engine.input.device.feature.Trigger1f;
import org.joml.Vector3f;

/**
 * A GLFW joystick adapter for an {@link XboxController}.
 * 
 * @see GLFWJoystickAdapter
 */
public class GLFWXboxControllerAdapter
		extends GLFWJoystickAdapter<XboxController> {

	/* @formatter: off */
	@AdapterMapping
	private static final GLFWAnalogMapping<?>
			LS = new GLFWAnalogStickMapping(XboxController.LS, 0, 1),
			RS = new GLFWAnalogStickMapping(XboxController.RS, 2, 3),
			LT = new GLFWAnalogTriggerMapping(XboxController.LT, 4),
			RT = new GLFWAnalogTriggerMapping(XboxController.RT, 5);

	@AdapterMapping
	private static final GLFWButtonMapping
			A = new GLFWButtonMapping(XboxController.A, 0),
			B = new GLFWButtonMapping(XboxController.B, 1),
			X = new GLFWButtonMapping(XboxController.X, 2),
			Y = new GLFWButtonMapping(XboxController.Y, 3),
			LB = new GLFWButtonMapping(XboxController.LB, 4),
			RB = new GLFWButtonMapping(XboxController.RB, 5),
			GUIDE = new GLFWButtonMapping(XboxController.GUIDE, 6),
			START = new GLFWButtonMapping(XboxController.START, 7),
			THUMB_L = new GLFWButtonMapping(XboxController.THUMB_L, 8),
			THUMB_R = new GLFWButtonMapping(XboxController.THUMB_R, 9),
			UP = new GLFWButtonMapping(XboxController.UP, 10),
			RIGHT = new GLFWButtonMapping(XboxController.RIGHT, 11),
			DOWN = new GLFWButtonMapping(XboxController.DOWN, 12),
			LEFT = new GLFWButtonMapping(XboxController.LEFT, 13);
	/* @formatter: on */

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
	@FeatureAdapter
	public void updateAnalogStick(GLFWAnalogStickMapping mapping,
			Vector3f stick) {
		super.updateAnalogStick(mapping, stick);
		if (mapping == LS || mapping == RS) {
			stick.y *= -1.0F;
		}
	}

	@Override
	@FeatureAdapter
	public void updateAnalogTrigger(GLFWAnalogTriggerMapping mapping,
			Trigger1f trigger) {
		super.updateAnalogTrigger(mapping, trigger);
		if (mapping == LT || mapping == RT) {
			trigger.force += 1.0F;
			trigger.force /= 2.0F;
		}
	}

}
