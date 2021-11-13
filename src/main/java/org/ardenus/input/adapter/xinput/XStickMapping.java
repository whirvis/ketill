package org.ardenus.input.adapter.xinput;

import org.ardenus.input.adapter.AnalogMapping;
import org.ardenus.input.feature.AnalogStick;

import com.github.strikerx3.jxinput.enums.XInputAxis;

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
