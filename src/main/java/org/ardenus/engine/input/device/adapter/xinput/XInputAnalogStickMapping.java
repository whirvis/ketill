package org.ardenus.engine.input.device.adapter.xinput;

import org.ardenus.engine.input.device.feature.AnalogStick;

import com.github.strikerx3.jxinput.enums.XInputAxis;

public class XInputAnalogStickMapping extends XInputAnalogMapping<AnalogStick> {

	public final XInputAxis xAxis;
	public final XInputAxis yAxis;

	public XInputAnalogStickMapping(AnalogStick analog, XInputAxis xAxis,
			XInputAxis yAxis) {
		super(analog);
		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}

}
