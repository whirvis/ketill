package org.ardenus.engine.input.device.adapter.gamecube;

import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.feature.DeviceAnalog;

public class USBGameCubeAnalogMapping<A extends DeviceAnalog<?>>
		extends AnalogMapping<A> {

	public USBGameCubeAnalogMapping(A analog) {
		super(analog);
	}

}
