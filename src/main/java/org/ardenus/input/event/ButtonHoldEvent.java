package org.ardenus.input.event;

import org.ardenus.input.InputDevice;
import org.ardenus.input.feature.DeviceButton;

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
