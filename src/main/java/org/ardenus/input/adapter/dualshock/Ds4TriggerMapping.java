package org.ardenus.input.adapter.dualshock;

import org.ardenus.input.adapter.AnalogMapping;
import org.ardenus.input.feature.AnalogTrigger;

public class Ds4TriggerMapping extends AnalogMapping<AnalogTrigger> {

	public final int byteOffset;
	
	public Ds4TriggerMapping(AnalogTrigger analog, int byteOffset) {
		super(analog);
		this.byteOffset = byteOffset;
	}

}
