package org.ardenus.engine.input.device.event;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.feature.DeviceButton;

public class ButtonPressEvent extends FeaturePressEvent {

	/**
	 * @param device
	 *            the device that pressed {@code button}.
	 * @param button
	 *            the button that was pressed.
	 * @param held
	 *            {@code true} if {@code button} is being held down,
	 *            {@code false} otherwise.
	 * @throws NullPointerException
	 *             if {@code device} or {@code button} are {@code null}.
	 */
	public ButtonPressEvent(InputDevice device, DeviceButton button,
			boolean held) {
		super(device, button, button.direction, held);
	}

	/**
	 * @return the button that was pressed.
	 */
	public DeviceButton getButton() {
		return (DeviceButton) this.getFeature();
	}

}
