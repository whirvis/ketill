package org.ardenus.engine.input.device.adapter.xinput;

import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.feature.DeviceAnalog;

public class XInputAnalogMapping<A extends DeviceAnalog<?>>
		extends AnalogMapping<A> {

	public XInputAnalogMapping(A analog) {
		super(analog);
	}

}
