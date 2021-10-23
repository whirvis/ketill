package org.ardenus.engine.input.device.adapter.xinput;

import org.ardenus.engine.input.device.feature.AnalogStick;

import com.github.strikerx3.jxinput.enums.XInputAxis;

public class XStickMapping extends XAnalogMapping<AnalogStick> {

	public final XInputAxis xAxis;
	public final XInputAxis yAxis;

	public XStickMapping(AnalogStick analog, XInputAxis xAxis,
			XInputAxis yAxis) {
		super(analog);
		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}

}
