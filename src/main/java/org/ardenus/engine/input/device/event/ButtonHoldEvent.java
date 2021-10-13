package org.ardenus.engine.input.device.event;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.feature.DeviceButton;

/**
 * Signals that an {@link InputDevice} has held down a {@link DeviceButton}.
 */
public class ButtonHoldEvent extends FeatureHoldEvent {

	/**
	 * Constructs a new {@code ButtonHoldEvent}.
	 * 
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
	 * Returns the button being held down.
	 * 
	 * @return the button being held down.
	 */
	public DeviceButton getButton() {
		return (DeviceButton) this.getFeature();
	}

}
