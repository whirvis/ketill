package com.whirvis.kibasan.adapter.dualshock;

import com.whirvis.controller.AnalogTrigger;
import com.whirvis.kibasan.adapter.AnalogMapping;

public class Ds4TriggerMapping extends AnalogMapping<AnalogTrigger> {

	public final int byteOffset;
	
	public Ds4TriggerMapping(AnalogTrigger analog, int byteOffset) {
		super(analog);
		this.byteOffset = byteOffset;
	}

}
