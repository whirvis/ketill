package com.whirvis.kibasan.dualshock;

import com.whirvis.controller.AnalogMapping;
import com.whirvis.controller.AnalogTrigger;

public class Ds4TriggerMapping extends AnalogMapping<AnalogTrigger> {

	public final int byteOffset;
	
	public Ds4TriggerMapping(AnalogTrigger analog, int byteOffset) {
		super(analog);
		this.byteOffset = byteOffset;
	}

}
