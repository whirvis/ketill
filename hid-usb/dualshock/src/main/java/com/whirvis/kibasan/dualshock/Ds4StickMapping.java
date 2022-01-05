package com.whirvis.kibasan.dualshock;

import com.whirvis.controller.AnalogMapping;
import com.whirvis.controller.AnalogStick;

public class Ds4StickMapping
		extends AnalogMapping<AnalogStick> {

	public final int byteOffsetX;
	public final int byteOffsetY;
	
	public Ds4StickMapping(AnalogStick stick, int byteOffsetX, int byteOffsetY) {
		super(stick);
		this.byteOffsetX = byteOffsetX;
		this.byteOffsetY = byteOffsetY;
	}

}
