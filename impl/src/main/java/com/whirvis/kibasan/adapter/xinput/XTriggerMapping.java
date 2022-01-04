package com.whirvis.kibasan.adapter.xinput;

import com.github.strikerx3.jxinput.enums.XInputAxis;
import com.whirvis.kibasan.adapter.AnalogMapping;
import com.whirvis.kibasan.feature.AnalogTrigger;

public class XTriggerMapping extends AnalogMapping<AnalogTrigger> {

	public final XInputAxis triggerAxis;

	public XTriggerMapping(AnalogTrigger analog, XInputAxis triggerAxis) {
		super(analog);
		this.triggerAxis = triggerAxis;
	}

}
