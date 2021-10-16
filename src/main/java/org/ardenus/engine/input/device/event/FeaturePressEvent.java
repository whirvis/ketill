package org.ardenus.engine.input.device.event;

import java.util.Objects;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.feature.DeviceFeature;

public class FeaturePressEvent extends DeviceEvent {

	private final DeviceFeature<?> feature;
	private final Direction direction;
	private final boolean held;

	/**
	 * @param device
	 *            the device that pressed {@code feature}.
	 * @param feature
	 *            the feature that was pressed.
	 * @param direction
	 *            the direction that {@code feature} represents (or is pressing
	 *            towards.) A value of {@code null} is permitted, and indicates
	 *            that {@code feature} represents no direction.
	 * @param held
	 *            {@code true} if {@code feature} is being held down,
	 *            {@code false} otherwise.
	 * @throws NullPointerException
	 *             if {@code device} or {@code feature} are {@code null}.
	 */
	public FeaturePressEvent(InputDevice device, DeviceFeature<?> feature,
			Direction direction, boolean held) {
		super(device);
		this.feature = Objects.requireNonNull(feature, "feature");
		this.direction = direction;
		this.held = held;
	}

	/**
	 * @return the feature that was pressed.
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

	/**
	 * @return {@code true} if the feature is being held down, {@code false}
	 *         otherwise.
	 */
	public boolean isHeld() {
		return this.held;
	}

}
