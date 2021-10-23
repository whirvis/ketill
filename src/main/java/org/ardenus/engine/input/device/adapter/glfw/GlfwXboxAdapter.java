package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.XboxController;
import org.ardenus.engine.input.device.adapter.AdapterMapping;
import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.ardenus.engine.input.device.feature.Trigger1f;
import org.joml.Vector3f;

public class GlfwXboxAdapter
		extends GlfwJoystickAdapter<XboxController> {

	/* @formatter: off */
	@AdapterMapping
	private static final AnalogMapping<?>
			LS = new GlfwStickMapping(XboxController.LS, 0, 1),
			RS = new GlfwStickMapping(XboxController.RS, 2, 3),
			LT = new GlfwTriggerMapping(XboxController.LT, 4),
			RT = new GlfwTriggerMapping(XboxController.RT, 5);

	@AdapterMapping
	private static final GlfwButtonMapping
			A = new GlfwButtonMapping(XboxController.A, 0),
			B = new GlfwButtonMapping(XboxController.B, 1),
			X = new GlfwButtonMapping(XboxController.X, 2),
			Y = new GlfwButtonMapping(XboxController.Y, 3),
			LB = new GlfwButtonMapping(XboxController.LB, 4),
			RB = new GlfwButtonMapping(XboxController.RB, 5),
			GUIDE = new GlfwButtonMapping(XboxController.GUIDE, 6),
			START = new GlfwButtonMapping(XboxController.START, 7),
			THUMB_L = new GlfwButtonMapping(XboxController.THUMB_L, 8),
			THUMB_R = new GlfwButtonMapping(XboxController.THUMB_R, 9),
			UP = new GlfwButtonMapping(XboxController.UP, 10),
			RIGHT = new GlfwButtonMapping(XboxController.RIGHT, 11),
			DOWN = new GlfwButtonMapping(XboxController.DOWN, 12),
			LEFT = new GlfwButtonMapping(XboxController.LEFT, 13);
	/* @formatter: on */

	public GlfwXboxAdapter(long ptr_glfwWindow, int glfwJoystick) {
		super(ptr_glfwWindow, glfwJoystick);
	}

	@Override
	@FeatureAdapter
	public void updateAnalogStick(GlfwStickMapping mapping,
			Vector3f stick) {
		super.updateAnalogStick(mapping, stick);
		if (mapping == LS || mapping == RS) {
			stick.y *= -1.0F;
		}
	}

	@Override
	@FeatureAdapter
	public void updateAnalogTrigger(GlfwTriggerMapping mapping,
			Trigger1f trigger) {
		super.updateAnalogTrigger(mapping, trigger);
		if (mapping == LT || mapping == RT) {
			trigger.force += 1.0F;
			trigger.force /= 2.0F;
		}
	}

}
