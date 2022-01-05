package com.whirvis.kibasan.dualshock;

import com.whirvis.controller.ButtonMapping;
import com.whirvis.controller.DeviceButton;

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
