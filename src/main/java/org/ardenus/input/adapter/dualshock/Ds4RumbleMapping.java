package org.ardenus.input.adapter.dualshock;

import org.ardenus.input.adapter.RumbleMapping;
import org.ardenus.input.feature.RumbleMotor;

public class Ds4RumbleMapping extends RumbleMapping {

	public final int byteOffset;

	public Ds4RumbleMapping(RumbleMotor motor, int byteOffset) {
		super(motor);
		this.byteOffset = byteOffset;
	}

}
