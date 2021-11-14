package com.whirvis.kibasan.seeker.event;

import java.util.Objects;

import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.seeker.DeviceSeeker;

public class DeviceUnregisterEvent extends DeviceSeekerEvent {

	private final InputDevice device;

	/**
	 * @param seeker
	 *            the seeker that unregistered {@code device}.
	 * @param device
	 *            the device that was unregistered.
	 * @throws NullPointerException
	 *             if {@code seeker} or {@code device} are {@code null}.
	 */
	public DeviceUnregisterEvent(DeviceSeeker seeker, InputDevice device) {
		super(seeker);
		this.device = Objects.requireNonNull(device, "device");
	}

	/**
	 * @return the device that was unregistered.
	 */
	public InputDevice getDevice() {
		return this.device;
	}

}
