package org.ardenus.engine.input.device.event;

import java.util.Objects;

import org.ardenus.engine.input.InputEvent;
import org.ardenus.engine.input.device.InputDevice;

/**
 * An event relating to an {@link InputDevice}.
 */
public class DeviceEvent extends InputEvent {

	private final InputDevice device;

	/**
	 * Constructs a new {@code DeviceEvent}.
	 * 
	 * @param device
	 *            the device that triggered the event.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public DeviceEvent(InputDevice device) {
		this.device = Objects.requireNonNull(device, "device");
	}

	/**
	 * Returns the device that triggered the event.
	 * 
	 * @return the device that triggered the event, guaranteed not to be
	 *         {@code null}.
	 */
	public InputDevice getDevice() {
		return this.device;
	}

}
