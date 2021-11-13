package org.ardenus.input.event;

import org.ardenus.input.InputDevice;
import org.ardenus.input.feature.DeviceButton;

public class ButtonReleaseEvent extends FeatureReleaseEvent {

	/**
	 * @param device
	 *            the device that released {@code button}.
	 * @param button
	 *            the button that was released.
	 * @param held
	 *            {@code true} if {@code button} was being held down,
	 *            {@code false} otherwise.
	 * @throws NullPointerException
	 *             if {@code device} or {@code button} are {@code null}.
	 */
	public ButtonReleaseEvent(InputDevice device, DeviceButton button,
			boolean held) {
		super(device, button, button.direction, held);
	}

	/**
	 * @return the button that was released.
	 */
	public DeviceButton getButton() {
		return (DeviceButton) this.getFeature();
	}

}
