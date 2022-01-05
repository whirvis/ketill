package com.whirvis.kibasan.adapter.xinput;

import com.github.strikerx3.jxinput.enums.XInputAxis;
import com.whirvis.controller.AnalogTrigger;
import com.whirvis.kibasan.adapter.AnalogMapping;

public class XTriggerMapping extends AnalogMapping<AnalogTrigger> {

	public final XInputAxis triggerAxis;

	public XTriggerMapping(AnalogTrigger analog, XInputAxis triggerAxis) {
		super(analog);
		this.triggerAxis = triggerAxis;
	}

}
