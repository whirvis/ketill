package org.ardenus.engine.input.device.event;

import java.util.Objects;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.feature.DeviceFeature;

public class FeatureReleaseEvent extends DeviceEvent {

	private final DeviceFeature<?> button;
	private final Direction direction;
	private final boolean held;

	/**
	 * @param device
	 *            the device that released {@code feature}.
	 * @param feature
	 *            the feature that was released.
	 * @param direction
	 *            the direction that {@code feature} represents (or was pressing
	 *            towards.) A value of {@code null} is permitted, and indicates
	 *            that {@code feature} represents no direction.
	 * @param held
	 *            {@code true} if {@code feature} was being held down,
	 *            {@code false} otherwise.
	 * @throws NullPointerException
	 *             if {@code device} or {@code feature} are {@code null}.
	 */
	public FeatureReleaseEvent(InputDevice device, DeviceFeature<?> feature,
			Direction direction, boolean held) {
		super(device);
		this.button = Objects.requireNonNull(feature, "feature");
		this.direction = direction;
		this.held = held;
	}

	/**
	 * @return the feature that was released.
	 */
	public DeviceFeature<?> getFeature() {
		return this.button;
	}

	/**
	 * @return the direction being release from, if any.
	 */
	public Direction getDirection() {
		return this.direction;
	}

	/**
	 * @return {@code true} if the feature was being held down, {@code false}
	 *         otherwise.
	 */
	public boolean wasHeld() {
		return this.held;
	}

}
