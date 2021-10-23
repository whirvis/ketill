package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.Ps4Controller;
import org.ardenus.engine.input.device.adapter.AdapterMapping;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.ardenus.engine.input.device.feature.Trigger1f;

public class GlfwPs4Adapter extends GlfwPsxAdapter<Ps4Controller> {

	/* @formatter: off */
	@AdapterMapping
	public static final GlfwButtonMapping
			SHARE = new GlfwButtonMapping(Ps4Controller.SHARE, 8),
			OPTIONS = new GlfwButtonMapping(Ps4Controller.OPTIONS, 9),
			PS = new GlfwButtonMapping(Ps4Controller.PS, 12),
			TPAD = new GlfwButtonMapping(Ps4Controller.TPAD, 13),
			UP = new GlfwButtonMapping(Ps4Controller.UP, 14),
			RIGHT = new GlfwButtonMapping(Ps4Controller.RIGHT, 15),
			DOWN = new GlfwButtonMapping(Ps4Controller.DOWN, 16),
			LEFT = new GlfwButtonMapping(Ps4Controller.LEFT, 17);

	@AdapterMapping
	public static final GlfwTriggerMapping
			LT = new GlfwTriggerMapping(Ps4Controller.LT, 3),
			RT = new GlfwTriggerMapping(Ps4Controller.RT, 4);
	/* @formatter: on */
	
	public GlfwPs4Adapter(long ptr_glfwWindow, int glfwJoystick) {
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
