package org.ardenus.engine.input.device.event;

import org.ardenus.engine.input.device.InputDevice;

public class DeviceDisconnectEvent extends DeviceEvent {

	/**
	 * @param device
	 *            the device that has disconnected.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public DeviceDisconnectEvent(InputDevice device) {
		super(device);
	}

}
