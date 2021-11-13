package org.ardenus.input.seeker.event;

import java.util.Objects;

import org.ardenus.input.InputDevice;
import org.ardenus.input.seeker.DeviceSeeker;

public class DeviceRegisterEvent extends DeviceSeekerEvent {

	private final InputDevice device;

	/**
	 * @param seeker
	 *            the seeker that registered {@code device}.
	 * @param device
	 *            the device that was registered.
	 * @throws NullPointerException
	 *             if {@code seeker} or {@code device} are {@code null}.
	 */
	public DeviceRegisterEvent(DeviceSeeker seeker, InputDevice device) {
		super(seeker);
		this.device = Objects.requireNonNull(device, "device");
	}

	/**
	 * @return the device that was registered.
	 */
	public InputDevice getDevice() {
		return this.device;
	}

}
