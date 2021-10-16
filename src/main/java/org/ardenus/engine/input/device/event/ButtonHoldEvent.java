package org.ardenus.engine.input.device.event;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.feature.DeviceButton;

public class ButtonHoldEvent extends FeatureHoldEvent {

	/**
	 * @param device
	 *            the device holding {@code button}.
	 * @param button
	 *            the button being held down.
	 * @throws NullPointerException
	 *             if {@code device} or {@code button} are {@code null}.
	 */
	public ButtonHoldEvent(InputDevice device, DeviceButton button) {
		super(device, button, button.direction);
	}

	/**
	 * @return the button being held down.
	 */
	public DeviceButton getButton() {
		return (DeviceButton) this.getFeature();
	}

}
