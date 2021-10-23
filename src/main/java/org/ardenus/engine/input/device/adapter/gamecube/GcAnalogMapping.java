package org.ardenus.engine.input.device.adapter.gamecube;

import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.feature.DeviceAnalog;

public class GcAnalogMapping<A extends DeviceAnalog<?>>
		extends AnalogMapping<A> {

	public GcAnalogMapping(A analog) {
		super(analog);
	}

}
