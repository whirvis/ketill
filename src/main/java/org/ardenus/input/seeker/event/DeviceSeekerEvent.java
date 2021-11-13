package org.ardenus.input.seeker.event;

import java.util.Objects;

import org.ardenus.input.seeker.DeviceSeeker;

import com.whirvex.event.Event;

public abstract class DeviceSeekerEvent implements Event {

	private final DeviceSeeker seeker;

	/**
	 * @param seeker
	 *            the device seeker that triggered the event.
	 * @throws NullPointerException
	 *             if {@code seeker} is {@code null}.
	 */
	public DeviceSeekerEvent(DeviceSeeker seeker) {
		this.seeker = Objects.requireNonNull(seeker, "seeker");
	}

	/**
	 * @return the device seeker that triggered the event.
	 */
	public DeviceSeeker getSeeker() {
		return this.seeker;
	}

}
