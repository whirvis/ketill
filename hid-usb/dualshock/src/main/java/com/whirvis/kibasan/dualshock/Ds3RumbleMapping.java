package com.whirvis.kibasan.dualshock;

import com.whirvis.controller.RumbleMapping;
import com.whirvis.controller.RumbleMotor;

public class Ds3RumbleMapping extends RumbleMapping {

	public final int byteOffset;
	
	public Ds3RumbleMapping(RumbleMotor motor, int byteOffset) {
		super(motor);
		this.byteOffset = byteOffset;
	}

}
