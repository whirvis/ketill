package org.ardenus.input.event;

import java.util.Objects;

import org.ardenus.input.Direction;
import org.ardenus.input.InputDevice;
import org.ardenus.input.feature.AnalogStick;

public class StickHoldEvent extends FeatureHoldEvent {

	/**
	 * @param device
	 *            the device holding {@code stick}.
	 * @param stick
	 *            the stick being held down.
	 * @param direction
	 *            the direction that {@code stick} is pressed towards.
	 * @throws NullPointerException
	 *             if {@code device}, {@code stick}, or {@code direction} are
	 *             {@code null}.
	 */
	public StickHoldEvent(InputDevice device, AnalogStick stick,
			Direction direction) {
		super(device, stick, direction);
		Objects.requireNonNull(direction, "direction");
	}

	/**
	 * @return the stick being held down.
	 */
	public AnalogStick getStick() {
		return (AnalogStick) this.getFeature();
	}

}
