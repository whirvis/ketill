package com.whirvis.kibasan.adapter.glfw;

import com.whirvis.kibasan.AdapterMapping;
import com.whirvis.kibasan.FeatureAdapter;
import com.whirvis.kibasan.psx.PsxController;
import org.joml.Vector3f;

public abstract class GlfwPsxAdapter<I extends PsxController> extends GlfwJoystickAdapter<I> {

	/* @formatter: off */
	@AdapterMapping
	public static final GlfwButtonMapping
			SQUARE = new GlfwButtonMapping(PsxController.SQUARE, 0),
			CROSS = new GlfwButtonMapping(PsxController.CROSS, 1),
			CIRCLE = new GlfwButtonMapping(PsxController.CIRCLE, 2),
			TRIANGLE = new GlfwButtonMapping(PsxController.TRIANGLE, 3),
			L1 = new GlfwButtonMapping(PsxController.L1, 4),
			R1 = new GlfwButtonMapping(PsxController.R1, 5),
			L2 = new GlfwButtonMapping(PsxController.L2, 6),
			R2 = new GlfwButtonMapping(PsxController.R2, 7),
			THUMB_L = new GlfwButtonMapping(PsxController.THUMB_L, 10),
			THUMB_R = new GlfwButtonMapping(PsxController.THUMB_R, 11);
	
	@AdapterMapping
	public static final GlfwStickMapping
			LS = new GlfwStickMapping(PsxController.LS, 0, 1),
			RS = new GlfwStickMapping(PsxController.RS, 2, 5);
	/* @formatter: on */

	public GlfwPsxAdapter(long ptr_glfwWindow, int glfwJoystick) {
		super(ptr_glfwWindow, glfwJoystick);
	}

	@Override
	@FeatureAdapter
	public void updateStick(GlfwStickMapping mapping, Vector3f stick) {
		super.updateStick(mapping, stick);
		if (mapping == LS || mapping == RS) {
			stick.y *= -1.0F;
		}
	}

}
