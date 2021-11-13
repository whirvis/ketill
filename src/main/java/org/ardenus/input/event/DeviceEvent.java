package org.ardenus.input.event;

import java.util.Objects;

import org.ardenus.input.InputDevice;

public abstract class DeviceEvent extends InputEvent {

	private final InputDevice device;

	/**
	 * @param device
	 *            the device that triggered the event.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public DeviceEvent(InputDevice device) {
		this.device = Objects.requireNonNull(device, "device");
	}

	/**
	 * @return the device that triggered the event.
	 */
	public InputDevice getDevice() {
		return this.device;
	}

}
