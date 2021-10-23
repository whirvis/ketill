package org.ardenus.engine.input.device.adapter.gamecube;

import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.feature.AnalogTrigger;

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
