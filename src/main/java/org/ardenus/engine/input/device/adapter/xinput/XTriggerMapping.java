package org.ardenus.engine.input.device.adapter.xinput;

import org.ardenus.engine.input.device.feature.AnalogTrigger;

import com.github.strikerx3.jxinput.enums.XInputAxis;

public class XTriggerMapping
		extends XAnalogMapping<AnalogTrigger> {

	public final XInputAxis triggerAxis;

	public XTriggerMapping(AnalogTrigger analog,
			XInputAxis triggerAxis) {
		super(analog);
		this.triggerAxis = triggerAxis;
	}

}
