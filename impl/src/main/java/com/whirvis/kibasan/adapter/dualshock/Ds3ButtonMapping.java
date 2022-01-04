package com.whirvis.kibasan.adapter.dualshock;

import com.whirvis.controller.DeviceButton;
import com.whirvis.kibasan.adapter.ButtonMapping;

public class Ds3ButtonMapping extends ButtonMapping {

	public final int byteOffset;
	public final int bitIndex;

	public Ds3ButtonMapping(DeviceButton button, int byteOffset,
			int bitOffset) {
		super(button);
		this.byteOffset = byteOffset;
		this.bitIndex = bitOffset;
	}

}
