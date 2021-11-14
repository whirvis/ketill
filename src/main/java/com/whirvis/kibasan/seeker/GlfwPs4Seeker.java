package com.whirvis.kibasan.seeker;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.Ps4Controller;
import com.whirvis.kibasan.adapter.glfw.GlfwPs4Adapter;

public class GlfwPs4Seeker extends GlfwJoystickSeeker {

	private boolean wasAmbigous;

	public GlfwPs4Seeker(EventManager events, long ptr_glfwWindow) {
		super(Ps4Controller.class, events, ptr_glfwWindow);
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		return new Ps4Controller(events,
				new GlfwPs4Adapter(ptr_glfwWindow, glfwJoystick));
	}

	@Override
	public void poll() {
		super.poll();

		/*
		 * PlayStation 4 controllers have a tendency to report themselves as
		 * both USB and Bluetooth controllers at the same time. When this
		 * happens, there is no way to tell which controller is the same
		 * physical device. As such, the best course of action is to send an
		 * event, notifying listeners of the ambiguity.
		 */
		boolean isAmbigous = this.registered().size() > 1;
		if (!wasAmbigous && isAmbigous) {
			events.send(new Ps4Controller.AmbigousEvent(false));
			log.warn("Multiple PS4 controllers connected, "
					+ "physical devices are ambigous");
		} else if (wasAmbigous && !isAmbigous) {
			events.send(new Ps4Controller.AmbigousEvent(true));
			log.info("PS4 controllers are no longer ambigous");
		}
		this.wasAmbigous = isAmbigous;
	}

}
