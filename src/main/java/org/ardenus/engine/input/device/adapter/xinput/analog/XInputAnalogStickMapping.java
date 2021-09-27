package org.ardenus.engine.input.device.adapter.xinput.analog;

import org.ardenus.engine.input.device.adapter.xinput.XInputDeviceAdapter;
import org.ardenus.engine.input.device.analog.AnalogStick;

import com.github.strikerx3.jxinput.enums.XInputAxis;

/**
 * An {@link AnalogStick} mapping for use with an {@link XInputDeviceAdapter}.
 */
public class XInputAnalogStickMapping extends XInputAnalogMapping<AnalogStick> {

	public final XInputAxis xAxis;
	public final XInputAxis yAxis;

	/**
	 * Constructs a new {@code XInputMappedAnalogStick}.
	 * 
	 * @param analog
	 *            the stick being mapped to.
	 * @param glfwAxisX
	 *            the X-input stick X-axis.
	 * @param glfwAxisY
	 *            the X-input stick Y-axis.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	public XInputAnalogStickMapping(AnalogStick analog, XInputAxis xAxis,
			XInputAxis yAxis) {
		super(analog);
		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}

}
