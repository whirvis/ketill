package org.ardenus.engine.input.device.adapter.dualshock;

import org.ardenus.engine.input.device.adapter.RumbleMapping;
import org.ardenus.engine.input.device.feature.RumbleMotor;

public class Ds4RumbleMapping extends RumbleMapping {

	public final int byteOffset;

	public Ds4RumbleMapping(RumbleMotor motor, int byteOffset) {
		super(motor);
		this.byteOffset = byteOffset;
	}

}
