package org.ardenus.engine.input.device.event;

import java.util.Objects;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.Controller;
import org.ardenus.engine.input.device.feature.DeviceAnalog;

/**
 * Signals that a {@link DeviceAnalog} has been pressed towards a direction.
 */
public class AnalogPressEvent extends FeaturePressEvent {

	/**
	 * Constructs a new {@code AnalogPressEvent}.
	 * 
	 * @param controller
	 *            the controller that pressed {@code analog}.
	 * @param analog
	 *            the analog that was pressed.
	 * @param direction
	 *            the direction that {@code analog} was pressed toward.
	 * @param held
	 *            {@code true} if {@code analog} is being held down,
	 *            {@code false} otherwise.
	 * @throws NullPointerException
	 *             if {@code controller}, {@code analog}, or {@code direction}
	 *             are {@code null}.
	 */
	public AnalogPressEvent(Controller controller, DeviceAnalog<?> analog,
			Direction direction, boolean held) {
		super(controller, analog, direction, held);
		Objects.requireNonNull(direction, "direction");
	}

	/**
	 * Returns the analog that was pressed.
	 * <p>
	 * This method is simply a shorthand for calling {@link #getFeature()} with
	 * a cast done to convert the return value to a {@code DeviceAnalog}.
	 * 
	 * @return the analog that was pressed, guaranteed not to be {@code null}.
	 */
	public DeviceAnalog<?> getAnalog() {
		return (DeviceAnalog<?>) this.getFeature();
	}

}
