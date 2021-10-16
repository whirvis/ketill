package org.ardenus.engine.input.device.event;

import java.util.Objects;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.feature.DeviceFeature;

public class FeatureHoldEvent extends DeviceEvent {

	private final DeviceFeature<?> feature;
	private final Direction direction;

	/**
	 * @param device
	 *            the device holding {@code feature}.
	 * @param feature
	 *            the feature being held down.
	 * @param direction
	 *            the direction that {@code feature} represents (or is pressing
	 *            towards.) A value of {@code null} is permitted, and indicates
	 *            that {@code feature} represents no direction.
	 * @throws NullPointerException
	 *             if {@code device} or {@code feature} are {@code null}.
	 */
	public FeatureHoldEvent(InputDevice device, DeviceFeature<?> feature,
			Direction direction) {
		super(device);
		this.feature = Objects.requireNonNull(feature, "feature");
		this.direction = direction;
	}

	/**
	 * @return the feature being held down.
	 */
	public DeviceFeature<?> getFeature() {
		return this.feature;
	}

	/**
	 * @return the direction being pressed toward, if any.
	 */
	public Direction getDirection() {
		return this.direction;
	}

}
