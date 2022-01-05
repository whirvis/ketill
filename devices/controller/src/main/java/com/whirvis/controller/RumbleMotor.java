package com.whirvis.controller;

import com.whirvis.kibasan.DeviceFeature;

import java.util.Objects;

public class RumbleMotor extends DeviceFeature<Vibration1f> {

	/**
	 * @param id
	 *            the rumble motor ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public RumbleMotor(String id) {
		super(id, Vibration1f::new);
	}

}
