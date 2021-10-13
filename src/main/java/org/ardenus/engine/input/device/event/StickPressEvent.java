package org.ardenus.engine.input.device.event;

import java.util.Objects;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.feature.AnalogStick;

/**
 * Signals that an {@link InputDevice} has pressed an {@link AnalogStick}.
 */
public class StickPressEvent extends FeaturePressEvent {

	/**
	 * Constructs a new {@code StickPressEvent}.
	 * 
	 * @param device
	 *            the device that pressed {@code stick}.
	 * @param stick
	 *            the stick that was pressed.
	 * @param direction
	 *            the direction that {@code stick} is pressed towards.
	 * @param held
	 *            {@code true} if {@code stick} is being held down,
	 *            {@code false} otherwise.
	 * @throws NullPointerException
	 *             if {@code device}, {@code stick}, {@code direction} are
	 *             {@code null}.
	 */
	public StickPressEvent(InputDevice device, AnalogStick stick,
			Direction direction, boolean held) {
		super(device, stick, direction, held);
		Objects.requireNonNull(direction, "direction");
	}

	/**
	 * Returns the stick that was pressed.
	 * 
	 * @return the stick that was pressed.
	 */
	public AnalogStick getStick() {
		return (AnalogStick) this.getFeature();
	}

}
