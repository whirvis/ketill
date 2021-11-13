package org.ardenus.input.event;

import java.util.Objects;

import org.ardenus.input.Direction;
import org.ardenus.input.InputDevice;
import org.ardenus.input.feature.AnalogStick;

public class StickReleaseEvent extends FeatureReleaseEvent {

	/**
	 * @param device
	 *            the device that released {@code stick}.
	 * @param stick
	 *            the stick that was released.
	 * @param direction
	 *            the direction that {@code stick} was pressed towards.
	 * @param held
	 *            {@code true} if {@code stick} was being held down,
	 *            {@code false} otherwise.
	 * @throws NullPointerException
	 *             if {@code device}, {@code stick}, {@code direction} are
	 *             {@code null}.
	 */
	public StickReleaseEvent(InputDevice device, AnalogStick stick,
			Direction direction, boolean held) {
		super(device, stick, direction, held);
		Objects.requireNonNull(direction, "direction");
	}

	/**
	 * @return the stick that was released.
	 */
	public AnalogStick getStick() {
		return (AnalogStick) this.getFeature();
	}

}
