package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.Input;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.Ps4Controller;
import org.ardenus.engine.input.device.adapter.glfw.GlfwPs4Adapter;

public class GlfwPs4Seeker extends GlfwJoystickSeeker {

	private boolean wasAmbigous;

	public GlfwPs4Seeker(long ptr_glfwWindow) {
		super(Ps4Controller.class, ptr_glfwWindow);
	}

	@Override
	protected InputDevice createDevice(long ptr_glfwWindow, int glfwJoystick) {
		return new Ps4Controller(
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
			Input.sendEvent(new Ps4Controller.AmbigousEvent(false));
		} else if (wasAmbigous && !isAmbigous) {
			Input.sendEvent(new Ps4Controller.AmbigousEvent(true));
		}
		this.wasAmbigous = isAmbigous;
	}

}
