package org.ardenus.engine.input.device.adapter.xinput.analog;

import org.ardenus.engine.input.device.adapter.xinput.XInputDeviceAdapter;
import org.ardenus.engine.input.device.feature.AnalogTrigger;

import com.github.strikerx3.jxinput.enums.XInputAxis;

/**
 * An {@link AnalogTrigger} mapping for use with an {@link XInputDeviceAdapter}.
 */
public class XInputAnalogTriggerMapping
		extends XInputAnalogMapping<AnalogTrigger> {

	public final XInputAxis triggerAxis;

	/**
	 * Constructs a new {@code XInputMappedAnalogTrigger}.
	 * 
	 * @param analog
	 *            the trigger being mapped to.
	 * @param glfwAxis
	 *            the X-input trigger axis.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	public XInputAnalogTriggerMapping(AnalogTrigger analog,
			XInputAxis triggerAxis) {
		super(analog);
		this.triggerAxis = triggerAxis;
	}

}
