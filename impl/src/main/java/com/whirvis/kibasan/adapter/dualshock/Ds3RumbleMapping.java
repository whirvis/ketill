package com.whirvis.kibasan.adapter.dualshock;

import com.whirvis.controller.RumbleMotor;
import com.whirvis.kibasan.adapter.RumbleMapping;

public class Ds3RumbleMapping extends RumbleMapping {

	public final int byteOffset;
	
	public Ds3RumbleMapping(RumbleMotor motor, int byteOffset) {
		super(motor);
		this.byteOffset = byteOffset;
	}

}
