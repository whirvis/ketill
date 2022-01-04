package com.whirvis.kibasan.adapter.glfw;

import com.whirvis.kibasan.AdapterMapping;
import com.whirvis.kibasan.FeatureAdapter;
import com.whirvis.kibasan.SwitchProController;
import org.joml.Vector3f;

public class GlfwSwitchProAdapter extends GlfwJoystickAdapter<SwitchProController> {

	/* @formatter: off */
	@AdapterMapping
	public static final GlfwButtonMapping
			B = new GlfwButtonMapping(SwitchProController.B, 0),
			A = new GlfwButtonMapping(SwitchProController.A, 1),
			Y = new GlfwButtonMapping(SwitchProController.Y, 2),
			X = new GlfwButtonMapping(SwitchProController.X, 3),
			L = new GlfwButtonMapping(SwitchProController.L, 4),
			R = new GlfwButtonMapping(SwitchProController.R, 5),
			ZL = new GlfwButtonMapping(SwitchProController.ZL, 6),
			ZR = new GlfwButtonMapping(SwitchProController.ZR, 7),
			MINUS = new GlfwButtonMapping(SwitchProController.MINUS, 8),
			PLUS = new GlfwButtonMapping(SwitchProController.PLUS, 9),
			THUMB_L = new GlfwButtonMapping(SwitchProController.THUMB_L, 10),
			THUMB_R = new GlfwButtonMapping(SwitchProController.THUMB_R, 11),
			HOME = new GlfwButtonMapping(SwitchProController.HOME, 12),
			SCREENSHOT = new GlfwButtonMapping(SwitchProController.SCREENSHOT, 13),
			BUMPER = new GlfwButtonMapping(SwitchProController.BUMPER, 14),
			Z_BUMPER = new GlfwButtonMapping(SwitchProController.Z_BUMPER, 15),
			UP = new GlfwButtonMapping(SwitchProController.UP, 16),
			RIGHT = new GlfwButtonMapping(SwitchProController.RIGHT, 17),
			DOWN = new GlfwButtonMapping(SwitchProController.DOWN, 18),
			LEFT = new GlfwButtonMapping(SwitchProController.LEFT, 19);
	
	@AdapterMapping
	public static final GlfwStickMapping
			LS = new GlfwStickMapping(SwitchProController.LS, 0, 1),
			RS = new GlfwStickMapping(SwitchProController.RS, 2, 3);
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

	public GlfwSwitchProAdapter(long ptr_glfwWindow, int glfwJoystick) {
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
