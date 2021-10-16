package org.ardenus.engine.input.device.event;

import java.util.Objects;

import org.ardenus.engine.input.InputEvent;
import org.ardenus.engine.input.device.InputDevice;

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
