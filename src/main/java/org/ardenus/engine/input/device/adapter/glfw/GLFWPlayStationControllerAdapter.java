package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.PlayStationController;
import org.ardenus.engine.input.device.adapter.AdapterMapping;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.ardenus.engine.input.device.feature.Trigger1f;
import org.joml.Vector3f;

/**
 * A GLFW joystick adapter for a {@link PlayStationController}.
 * 
 * @see GLFWJoystickAdapter
 */
public class GLFWPlayStationControllerAdapter
		extends GLFWJoystickAdapter<PlayStationController> {

	/* @formatter: off */
	@AdapterMapping
	public static final GLFWAnalogMapping<?>
			LS = new GLFWAnalogStickMapping(PlayStationController.LS, 0, 1),
			RS = new GLFWAnalogStickMapping(PlayStationController.RS, 2, 5),
			LT = new GLFWAnalogTriggerMapping(PlayStationController.LT, 3),
			RT = new GLFWAnalogTriggerMapping(PlayStationController.RT, 4);
	
	@AdapterMapping
	public static final GLFWButtonMapping
			SQUARE = new GLFWButtonMapping(PlayStationController.SQUARE, 0),
			CROSS = new GLFWButtonMapping(PlayStationController.CROSS, 1),
			CIRCLE = new GLFWButtonMapping(PlayStationController.CIRCLE, 2),
			TRIANGLE = new GLFWButtonMapping(PlayStationController.TRIANGLE, 3),
			L1 = new GLFWButtonMapping(PlayStationController.L1, 4),
			R1 = new GLFWButtonMapping(PlayStationController.R1, 5),
			L2 = new GLFWButtonMapping(PlayStationController.L2, 6),
			R2 = new GLFWButtonMapping(PlayStationController.R2, 7),
			SHARE = new GLFWButtonMapping(PlayStationController.SHARE, 8),
			OPTIONS = new GLFWButtonMapping(PlayStationController.OPTIONS, 9),
			THUMB_L = new GLFWButtonMapping(PlayStationController.THUMB_L, 10),
			THUMB_R = new GLFWButtonMapping(PlayStationController.THUMB_R, 11),
			PS = new GLFWButtonMapping(PlayStationController.PS, 12),
			TPAD = new GLFWButtonMapping(PlayStationController.TPAD, 13),
			UP = new GLFWButtonMapping(PlayStationController.UP, 14),
			RIGHT = new GLFWButtonMapping(PlayStationController.RIGHT, 15),
			DOWN = new GLFWButtonMapping(PlayStationController.DOWN, 16),
			LEFT = new GLFWButtonMapping(PlayStationController.LEFT, 17);
	/* @formatter: on */

	/**
	 * Constructs a new {@code GLFWPlayStationControllerAdapter}.
	 * 
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @param glfwJoystick
	 *            the GLFW joystick ID.
	 * @throws InputException
	 *             if an input error occurs.
	 */
	public GLFWPlayStationControllerAdapter(long ptr_glfwWindow,
			int glfwJoystick) {
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
