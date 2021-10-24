package org.ardenus.engine.input.device.adapter.dualshock;

import org.ardenus.engine.input.device.adapter.ButtonMapping;
import org.ardenus.engine.input.device.feature.DeviceButton;

public class Ds4ButtonMapping extends ButtonMapping {

	public final int byteOffset;
	public final int bitIndex;

	public Ds4ButtonMapping(DeviceButton button, int byteOffset,
			int bitOffset) {
		super(button);
		this.byteOffset = byteOffset;
		this.bitIndex = bitOffset;
	}

}
