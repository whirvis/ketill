package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.SwitchController;
import org.ardenus.engine.input.device.adapter.AdapterMapping;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.joml.Vector3f;

public class GlfwSwitchAdapter extends GlfwJoystickAdapter<SwitchController> {

	/* @formatter: off */
	@AdapterMapping
	public static final GlfwButtonMapping
			B = new GlfwButtonMapping(SwitchController.B, 0),
			A = new GlfwButtonMapping(SwitchController.A, 1),
			Y = new GlfwButtonMapping(SwitchController.Y, 2),
			X = new GlfwButtonMapping(SwitchController.X, 3),
			L = new GlfwButtonMapping(SwitchController.L, 4),
			R = new GlfwButtonMapping(SwitchController.R, 5),
			ZL = new GlfwButtonMapping(SwitchController.ZL, 6),
			ZR = new GlfwButtonMapping(SwitchController.ZR, 7),
			MINUS = new GlfwButtonMapping(SwitchController.MINUS, 8),
			PLUS = new GlfwButtonMapping(SwitchController.PLUS, 9),
			THUMB_L = new GlfwButtonMapping(SwitchController.THUMB_L, 10),
			THUMB_R = new GlfwButtonMapping(SwitchController.THUMB_R, 11),
			HOME = new GlfwButtonMapping(SwitchController.HOME, 12),
			SCREENSHOT = new GlfwButtonMapping(SwitchController.SCREENSHOT, 13),
			BUMPER = new GlfwButtonMapping(SwitchController.BUMPER, 14),
			Z_BUMPER = new GlfwButtonMapping(SwitchController.Z_BUMPER, 15),
			UP = new GlfwButtonMapping(SwitchController.UP, 16),
			RIGHT = new GlfwButtonMapping(SwitchController.RIGHT, 17),
			DOWN = new GlfwButtonMapping(SwitchController.DOWN, 18),
			LEFT = new GlfwButtonMapping(SwitchController.LEFT, 19);
	
	@AdapterMapping
	public static final GlfwStickMapping
			LS = new GlfwStickMapping(SwitchController.LS, 0, 1),
			RS = new GlfwStickMapping(SwitchController.RS, 2, 3);
	/* @formatter: on */

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

	public GlfwSwitchAdapter(long ptr_glfwWindow, int glfwJoystick) {
		super(ptr_glfwWindow, glfwJoystick);
	}

	@Override
	@FeatureAdapter
	public void updateStick(GlfwStickMapping mapping, Vector3f stick) {
		super.updateStick(mapping, stick);
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
