package org.ardenus.engine.input.device.adapter.xinput;

import java.util.Objects;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.adapter.ButtonMapping;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;

import com.github.strikerx3.jxinput.XInputDevice;

/**
 * An adapter which maps input for an X-input device.
 *
 * @param <I>
 *            the input device type.
 * @see XAnalogMapping
 * @see XButtonMapping
 */
public abstract class XInputDeviceAdapter<I extends InputDevice>
		extends DeviceAdapter<I> {

	protected final XInputDevice xinput;

	/**
	 * @param xinput
	 *            the X-input device.
	 * @throws NullPointerException
	 *             if {@code xinput} is {@code null}.
	 * @see #map(AnalogMapping)
	 * @see #map(ButtonMapping)
	 */
	public XInputDeviceAdapter(XInputDevice xinput) {
		this.xinput = Objects.requireNonNull(xinput, "xinput");
	}

	@Override
	public boolean isConnected() {
		return xinput.isConnected();
	}

	@Override
	public void poll() {
		xinput.poll();
	}

}
