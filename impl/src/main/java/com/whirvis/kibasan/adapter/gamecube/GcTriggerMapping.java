package com.whirvis.kibasan.adapter.gamecube;

import com.whirvis.controller.AnalogTrigger;
import com.whirvis.kibasan.adapter.AnalogMapping;

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
