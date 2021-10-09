package org.ardenus.engine.input.device.event;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.Controller;
import org.ardenus.engine.input.device.feature.DeviceButton;

/**
 * Signals that a {@link Controller} has released a {@link DeviceButton}.
 */
public class ButtonReleaseEvent extends FeatureReleaseEvent {

	/**
	 * Constructs a new {@code ButtonReleaseEvent}.
	 * 
	 * @param controller
	 *            the controller that released {@code button}.
	 * @param button
	 *            the button that was released.
	 * @param direction
	 *            the direction {@code button} represents. A value of
	 *            {@code null} is permitted, and indicates that {@code button}
	 *            represents no direction.
	 * @param held
	 *            {@code true} if {@code button} was being held down,
	 *            {@code false} otherwise.
	 * @throws NullPointerException
	 *             if {@code controller} or {@code button} are {@code null}.
	 */
	public ButtonReleaseEvent(Controller controller, DeviceButton button,
			Direction direction, boolean held) {
		super(controller, button, direction, held);
	}

	/**
	 * Returns the button that was released.
	 * <p>
	 * This method is simply a shorthand for calling {@link #getFeature()} with
	 * a cast done to convert the return value to a {@code DeviceButton}.
	 * 
	 * @return the button that was released, guaranteed not to be {@code null}.
	 */
	public DeviceButton getButton() {
		return (DeviceButton) this.getFeature();
	}

}
