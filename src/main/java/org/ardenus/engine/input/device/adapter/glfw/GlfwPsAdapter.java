package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.PsController;
import org.ardenus.engine.input.device.adapter.AdapterMapping;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.ardenus.engine.input.device.feature.Trigger1f;
import org.joml.Vector3f;

public class GlfwPsAdapter extends GlfwJoystickAdapter<PsController> {

	/* @formatter: off */
	@AdapterMapping
	public static final GlfwButtonMapping
			SQUARE = new GlfwButtonMapping(PsController.SQUARE, 0),
			CROSS = new GlfwButtonMapping(PsController.CROSS, 1),
			CIRCLE = new GlfwButtonMapping(PsController.CIRCLE, 2),
			TRIANGLE = new GlfwButtonMapping(PsController.TRIANGLE, 3),
			L1 = new GlfwButtonMapping(PsController.L1, 4),
			R1 = new GlfwButtonMapping(PsController.R1, 5),
			L2 = new GlfwButtonMapping(PsController.L2, 6),
			R2 = new GlfwButtonMapping(PsController.R2, 7),
			SHARE = new GlfwButtonMapping(PsController.SHARE, 8),
			OPTIONS = new GlfwButtonMapping(PsController.OPTIONS, 9),
			THUMB_L = new GlfwButtonMapping(PsController.THUMB_L, 10),
			THUMB_R = new GlfwButtonMapping(PsController.THUMB_R, 11),
			PS = new GlfwButtonMapping(PsController.PS, 12),
			TPAD = new GlfwButtonMapping(PsController.TPAD, 13);
	
	@AdapterMapping
	public static final GlfwStickMapping
			LS = new GlfwStickMapping(PsController.LS, 0, 1),
			RS = new GlfwStickMapping(PsController.RS, 2, 5);
	
	@AdapterMapping
	public static final GlfwTriggerMapping
			LT = new GlfwTriggerMapping(PsController.LT, 3),
			RT = new GlfwTriggerMapping(PsController.RT, 4);
	/* @formatter: on */

	public GlfwPsAdapter(long ptr_glfwWindow, int glfwJoystick, boolean ps5) {
		super(ptr_glfwWindow, glfwJoystick);
		if (ps5) {
			this.mapPS5();
		} else {
			this.mapPS4();
		}
	}

	private void mapPS4() {
		this.map(new GlfwButtonMapping(PsController.UP, 14));
		this.map(new GlfwButtonMapping(PsController.RIGHT, 15));
		this.map(new GlfwButtonMapping(PsController.DOWN, 16));
		this.map(new GlfwButtonMapping(PsController.LEFT, 17));
	}

	private void mapPS5() {
		this.map(new GlfwButtonMapping(PsController.MUTE, 14));
		this.map(new GlfwButtonMapping(PsController.UP, 15));
		this.map(new GlfwButtonMapping(PsController.RIGHT, 16));
		this.map(new GlfwButtonMapping(PsController.DOWN, 17));
		this.map(new GlfwButtonMapping(PsController.LEFT, 18));
	}

	@Override
	@FeatureAdapter
	public void updateStick(GlfwStickMapping mapping, Vector3f stick) {
		super.updateStick(mapping, stick);
		if (mapping == LS || mapping == RS) {
			stick.y *= -1.0F;
		}
	}

	@Override
	@FeatureAdapter
	public void updateTrigger(GlfwTriggerMapping mapping, Trigger1f trigger) {
		super.updateTrigger(mapping, trigger);
		if (mapping == LT || mapping == RT) {
			trigger.force += 1.0F;
			trigger.force /= 2.0F;
		}
	}

}
