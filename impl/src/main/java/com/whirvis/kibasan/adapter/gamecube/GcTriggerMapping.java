package com.whirvis.kibasan.adapter.gamecube;

import com.whirvis.kibasan.adapter.AnalogMapping;
import com.whirvis.kibasan.feature.AnalogTrigger;

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
