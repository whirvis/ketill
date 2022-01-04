package com.whirvis.kibasan.adapter.dualshock;

import com.whirvis.controller.DeviceButton;
import com.whirvis.kibasan.adapter.ButtonMapping;

public class Ds4DpadMapping extends ButtonMapping {

	public final int byteOffset;
	public final int[] patterns;

	public Ds4DpadMapping(DeviceButton button, int byteOffset,
			int... patterns) {
		super(button);
		this.byteOffset = byteOffset;
		this.patterns = patterns;
	}

	public boolean hasPattern(int bits) {
		for (int pattern : patterns) {
			if (pattern == bits) {
				return true;
			}
		}
		return false;
	}

}
