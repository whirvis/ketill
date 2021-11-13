package org.ardenus.input.adapter.xinput;

import org.ardenus.input.adapter.AnalogMapping;
import org.ardenus.input.feature.AnalogTrigger;

import com.github.strikerx3.jxinput.enums.XInputAxis;

public class XTriggerMapping extends AnalogMapping<AnalogTrigger> {

	public final XInputAxis triggerAxis;

	public XTriggerMapping(AnalogTrigger analog, XInputAxis triggerAxis) {
		super(analog);
		this.triggerAxis = triggerAxis;
	}

}
