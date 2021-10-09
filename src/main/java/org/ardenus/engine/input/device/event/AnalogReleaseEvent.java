package org.ardenus.engine.input.device.event;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.Controller;
import org.ardenus.engine.input.device.feature.DeviceAnalog;

/**
 * Signals that a {@link DeviceAnalog} has been released from a direction.
 */
public class AnalogReleaseEvent extends FeatureReleaseEvent {

	/**
	 * Constructs a new {@code AnalogReleasedEvent}.
	 * 
	 * @param controller
	 *            the controller that released {@code analog}.
	 * @param analog
	 *            the analog that was released.
	 * @param direction
	 *            the direction that {@code analog} was released from.
	 * @param held
	 *            {@code true} if {@code analog} was being held down,
	 *            {@code false} otherwise.
	 * @throws NullPointerException
	 *             if {@code controller}, {@code analog}, or {@code direction}
	 *             are {@code null}.
	 */
	public AnalogReleaseEvent(Controller controller, DeviceAnalog<?> analog,
			Direction direction, boolean held) {
		super(controller, analog, direction, held);
	}

	/**
	 * Returns the analog that was released.
	 * <p>
	 * This method is simply a shorthand for calling {@link #getFeature()} with
	 * a cast done to convert the return value to a {@code DeviceAnalog}.
	 * 
	 * @return the analog that was released, guaranteed not to be {@code null}.
	 */
	public DeviceAnalog<?> getAnalog() {
		return (DeviceAnalog<?>) this.getFeature();
	}

}
