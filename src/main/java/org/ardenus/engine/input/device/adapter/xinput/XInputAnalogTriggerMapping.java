package org.ardenus.engine.input.device.adapter.xinput;

import org.ardenus.engine.input.device.feature.AnalogTrigger;

import com.github.strikerx3.jxinput.enums.XInputAxis;

public class XInputAnalogTriggerMapping
		extends XInputAnalogMapping<AnalogTrigger> {

	public final XInputAxis triggerAxis;

	public XInputAnalogTriggerMapping(AnalogTrigger analog,
			XInputAxis triggerAxis) {
		super(analog);
		this.triggerAxis = triggerAxis;
	}

}
