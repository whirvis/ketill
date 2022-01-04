package com.whirvis.kibasan.adapter.dualshock;

import com.whirvis.kibasan.adapter.ButtonMapping;
import com.whirvis.kibasan.feature.DeviceButton;

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
