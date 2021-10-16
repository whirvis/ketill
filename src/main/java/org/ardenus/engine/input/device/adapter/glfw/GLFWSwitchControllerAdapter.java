package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.SwitchController;
import org.ardenus.engine.input.device.adapter.AdapterMapping;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.joml.Vector3f;

public class GLFWSwitchControllerAdapter
		extends GLFWJoystickAdapter<SwitchController> {

	private static float normalize(float pos, float min, float max) {
		/*
		 * It's not uncommon for an axis to go one or two points outside of
		 * their usual minimum or maximum values. Clamping them will prevent
		 * return values outside the -1.0F to 1.0F range.
		 */
		if (pos < min) {
			pos = min;
		} else if (pos > max) {
			pos = max;
		}

		float mid = (max - min) / 2.0F;
		return (pos - min - mid) / mid;
	}

	/* @formatter: off */
	@AdapterMapping
	private static final GLFWAnalogMapping<?>
			LS = new GLFWAnalogStickMapping(SwitchController.LS, 0, 1),
			RS = new GLFWAnalogStickMapping(SwitchController.RS, 2, 3);
	
	@AdapterMapping
	private static final GLFWButtonMapping
			B = new GLFWButtonMapping(SwitchController.B, 0),
			A = new GLFWButtonMapping(SwitchController.A, 1),
			Y = new GLFWButtonMapping(SwitchController.Y, 2),
			X = new GLFWButtonMapping(SwitchController.X, 3),
			L = new GLFWButtonMapping(SwitchController.L, 4),
			R = new GLFWButtonMapping(SwitchController.R, 5),
			ZL = new GLFWButtonMapping(SwitchController.ZL, 6),
			ZR = new GLFWButtonMapping(SwitchController.ZR, 7),
			MINUS = new GLFWButtonMapping(SwitchController.MINUS, 8),
			PLUS = new GLFWButtonMapping(SwitchController.PLUS, 9),
			THUMB_L = new GLFWButtonMapping(SwitchController.THUMB_L, 10),
			THUMB_R = new GLFWButtonMapping(SwitchController.THUMB_R, 11),
			HOME = new GLFWButtonMapping(SwitchController.HOME, 12),
			SCREENSHOT = new GLFWButtonMapping(SwitchController.SCREENSHOT, 13),
			BUMPER = new GLFWButtonMapping(SwitchController.BUMPER, 14),
			Z_BUMPER = new GLFWButtonMapping(SwitchController.Z_BUMPER, 15),
			UP = new GLFWButtonMapping(SwitchController.UP, 16),
			RIGHT = new GLFWButtonMapping(SwitchController.RIGHT, 17),
			DOWN = new GLFWButtonMapping(SwitchController.DOWN, 18),
			LEFT = new GLFWButtonMapping(SwitchController.LEFT, 19);
	/* @formatter: on */

	public GLFWSwitchControllerAdapter(long ptr_glfwWindow, int glfwJoystick) {
		super(ptr_glfwWindow, glfwJoystick);
	}

	@Override
	@FeatureAdapter
	public void updateAnalogStick(GLFWAnalogStickMapping mapping,
			Vector3f stick) {
		super.updateAnalogStick(mapping, stick);
		if (mapping == LS) {
			stick.x = normalize(stick.x, -0.70F, 0.70F);
			stick.y = normalize(stick.y, -0.76F, 0.72F);
			stick.y *= -1.0F;
		} else if (mapping == RS) {
			stick.x = normalize(stick.x, -0.72F, 0.72F);
			stick.y = normalize(stick.y, -0.68F, 0.76F);
			stick.y *= -1.0F;
		}
	}

}
