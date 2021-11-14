package com.whirvis.kibasan.adapter.glfw;

import com.whirvis.kibasan.Ps5Controller;
import com.whirvis.kibasan.adapter.AdapterMapping;
import com.whirvis.kibasan.adapter.FeatureAdapter;
import com.whirvis.kibasan.feature.Trigger1f;

public class GlfwPs5Adapter extends GlfwPsxAdapter<Ps5Controller> {

	/* @formatter: off */
	@AdapterMapping
	public static final GlfwButtonMapping
			SHARE = new GlfwButtonMapping(Ps5Controller.SHARE, 8),
			OPTIONS = new GlfwButtonMapping(Ps5Controller.OPTIONS, 9),
			PS = new GlfwButtonMapping(Ps5Controller.PS, 12),
			TPAD = new GlfwButtonMapping(Ps5Controller.TPAD, 13),
			MUTE = new GlfwButtonMapping(Ps5Controller.MUTE, 14),
			UP = new GlfwButtonMapping(Ps5Controller.UP, 15),
			RIGHT = new GlfwButtonMapping(Ps5Controller.RIGHT, 16),
			DOWN = new GlfwButtonMapping(Ps5Controller.DOWN, 17),
			LEFT = new GlfwButtonMapping(Ps5Controller.LEFT, 18);
	
	@AdapterMapping
	public static final GlfwTriggerMapping
			LT = new GlfwTriggerMapping(Ps5Controller.LT, 3),
			RT = new GlfwTriggerMapping(Ps5Controller.RT, 4);
	/* @formatter: on */

	public GlfwPs5Adapter(long ptr_glfwWindow, int glfwJoystick) {
		super(ptr_glfwWindow, glfwJoystick);
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
