package org.ardenus.engine.input.device.event;

import org.ardenus.engine.input.device.InputDevice;

/**
 * Signals that an {@link InputDevice} has connected.
 */
public class DeviceConnectEvent extends DeviceEvent {

	/**
	 * Constructs a new {@code DeviceConnectEvent}.
	 * 
	 * @param device
	 *            the device that has connected.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public DeviceConnectEvent(InputDevice device) {
		super(device);
	}

}
