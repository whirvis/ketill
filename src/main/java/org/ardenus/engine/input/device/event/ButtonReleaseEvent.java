package org.ardenus.engine.input.device.event;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.feature.DeviceButton;

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
	public ButtonReleaseEvent(InputDevice controller, DeviceButton button,
			boolean held) {
		super(controller, button, button.direction, held);
	}

	/**
	 * @return the button that was released.
	 */
	public DeviceButton getButton() {
		return (DeviceButton) this.getFeature();
	}

}
