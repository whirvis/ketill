package com.whirvis.kibasan.event;

import com.whirvis.kibasan.InputDevice;

public class DeviceConnectEvent extends DeviceEvent {

	/**
	 * @param device
	 *            the device that has connected.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public DeviceConnectEvent(InputDevice device) {
		super(device);
	}

}
