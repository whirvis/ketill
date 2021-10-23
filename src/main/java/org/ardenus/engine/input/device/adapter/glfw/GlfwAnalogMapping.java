package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.feature.DeviceAnalog;

public class GlfwAnalogMapping<A extends DeviceAnalog<?>>
		extends AnalogMapping<A> {

	public GlfwAnalogMapping(A analog) {
		super(analog);
	}

}
