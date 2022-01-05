package com.whirvis.kibasan.xinput;

import com.github.strikerx3.jxinput.enums.XInputAxis;
import com.whirvis.controller.AnalogMapping;
import com.whirvis.controller.AnalogTrigger;

public class XTriggerMapping extends AnalogMapping<AnalogTrigger> {

	public final XInputAxis triggerAxis;

	public XTriggerMapping(AnalogTrigger analog, XInputAxis triggerAxis) {
		super(analog);
		this.triggerAxis = triggerAxis;
	}

}
