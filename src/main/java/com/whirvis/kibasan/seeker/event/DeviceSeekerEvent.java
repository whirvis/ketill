package com.whirvis.kibasan.seeker.event;

import java.util.Objects;

import com.whirvex.event.Event;
import com.whirvis.kibasan.seeker.DeviceSeeker;

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
