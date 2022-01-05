package com.whirvis.kibasan.gamecube;

import com.whirvis.controller.AnalogMapping;
import com.whirvis.controller.AnalogTrigger;

public class GcTriggerMapping extends AnalogMapping<AnalogTrigger> {

	public final int gcAxis;
	public final int min, max;

	public GcTriggerMapping(AnalogTrigger analog, int gcAxis, int min,
			int max) {
		super(analog);
		this.gcAxis = gcAxis;
		this.min = min;
		this.max = max;
	}

}
