package org.ardenus.engine.input.device.event;

import org.ardenus.engine.input.device.InputDevice;

/**
 * Signals that an {@link InputDevice} has connected.
 */
public class DeviceDisconnectEvent extends DeviceEvent {

	/**
	 * Constructs a new {@code DeviceDisconnectEvent}.
	 * 
	 * @param device
	 *            the device that has disconnected.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public DeviceDisconnectEvent(InputDevice device) {
		super(device);
	}

}
