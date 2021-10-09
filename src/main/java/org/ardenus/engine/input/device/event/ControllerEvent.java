package org.ardenus.engine.input.device.event;

import org.ardenus.engine.input.device.Controller;

/**
 * An event relating to a {@link Controller}.
 */
public abstract class ControllerEvent extends DeviceEvent {

	/**
	 * Constructs a new {@code ControllerEvent}.
	 * 
	 * @param controller
	 *            the controller that triggered the event.
	 * @throws NullPointerException
	 *             if {@code controller} is {@code null}.
	 */
	public ControllerEvent(Controller controller) {
		super(controller);
	}

	/**
	 * Returns the controller that triggered the event.
	 * <p>
	 * This method is simply a shorthand for calling {@link #getDevice()} with a
	 * cast done to convert the return value to a {@code Controller}.
	 * 
	 * @return the controller that triggered the event, guaranteed not to be
	 *         {@code null}.
	 */
	public Controller getController() {
		return (Controller) this.getDevice();
	}

}
