package com.whirvis.kibasan.adapter.xinput;

import com.github.strikerx3.jxinput.enums.XInputAxis;
import com.whirvis.kibasan.adapter.AnalogMapping;
import com.whirvis.kibasan.feature.AnalogStick;

public class XStickMapping extends AnalogMapping<AnalogStick> {

	public final XInputAxis xAxis;
	public final XInputAxis yAxis;

	public XStickMapping(AnalogStick analog, XInputAxis xAxis,
			XInputAxis yAxis) {
		super(analog);
		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}

}
