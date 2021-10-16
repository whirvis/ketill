package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.feature.DeviceAnalog;

public class GLFWAnalogMapping<A extends DeviceAnalog<?>>
		extends AnalogMapping<A> {

	public GLFWAnalogMapping(A analog) {
		super(analog);
	}

}
