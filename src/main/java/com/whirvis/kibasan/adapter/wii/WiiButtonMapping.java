package com.whirvis.kibasan.adapter.wii;

import com.whirvis.kibasan.adapter.ButtonMapping;
import com.whirvis.kibasan.feature.DeviceButton;

public class WiiButtonMapping extends ButtonMapping {

	public final int byteOffset;
	public final int bitIndex;

	public WiiButtonMapping(DeviceButton button, int byteOffset, int bitIndex) {
		super(button);
		this.byteOffset = byteOffset;
		this.bitIndex = bitIndex;
	}

}
