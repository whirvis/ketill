package org.ardenus.input.adapter.dualshock;

import org.ardenus.input.adapter.AnalogMapping;
import org.ardenus.input.feature.AnalogStick;

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
