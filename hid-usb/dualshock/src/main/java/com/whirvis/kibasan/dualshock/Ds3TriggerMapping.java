package com.whirvis.kibasan.dualshock;

import com.whirvis.controller.AnalogMapping;
import com.whirvis.controller.AnalogTrigger;

public class Ds3TriggerMapping extends AnalogMapping<AnalogTrigger> {

	public final int byteOffset;
	
	public Ds3TriggerMapping(AnalogTrigger analog, int byteOffset) {
		super(analog);
		this.byteOffset = byteOffset;
	}

}
