package org.ardenus.engine.input.device.event;

import java.util.Objects;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.Controller;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.feature.DeviceFeature;

/**
 * Signals that a {@link Controller} has pressed a {@link DeviceFeature}.
 */
public class FeaturePressEvent extends DeviceEvent {

	private final DeviceFeature<?> feature;
	private final Direction direction;
	private final boolean held;

	/**
	 * Constructs a new {@code ControllerPressEvent}.
	 * 
	 * @param controller
	 *            the controller that pressed {@code feature}.
	 * @param feature
	 *            the feature that was pressed.
	 * @param direction
	 *            the direction {@code feature} was pressed towards (or
	 *            represents). A value of {@code null} is permitted, and
	 *            indicates that {@code feature} represents no direction.
	 * @param held
	 *            {@code true} if {@code feature} is being held down,
	 *            {@code false} otherwise.
	 * @throws NullPointerException
	 *             if {@code controller} or {@code feature} are {@code null}.
	 */
	public FeaturePressEvent(InputDevice controller, DeviceFeature<?> feature,
			Direction direction, boolean held) {
		super(controller);
		this.feature = Objects.requireNonNull(feature, "feature");
		this.direction = direction;
		this.held = held;
	}

	/**
	 * Returns the feature that was pressed.
	 * 
	 * @return the feature that was pressed, guaranteed not to be {@code null}.
	 */
	public DeviceFeature<?> getFeature() {
		return this.feature;
	}

	/**
	 * Returns the direction pressed toward.
	 * 
	 * @return the direction pressed toward, if any.
	 */
	public Direction getDirection() {
		return this.direction;
	}

	/**
	 * Returns if the feature is being pressed towards a direction.
	 * 
	 * @param direction
	 *            the direction to check for.
	 * @return {@code true} if the feature is being pressed towards
	 *         {@code direction}, {@code false} otherwise.
	 */
	public boolean isDirection(Direction direction) {
		return this.direction == direction;
	}

	/**
	 * Returns if the feature is being held down.
	 * 
	 * @return {@code true} if the feature is being held down, {@code false}
	 *         otherwise.
	 */
	public boolean isHeld() {
		return this.held;
	}

}
