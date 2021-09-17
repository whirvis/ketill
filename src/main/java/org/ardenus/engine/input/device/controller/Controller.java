package org.ardenus.engine.input.device.controller;

import java.util.HashMap;
import java.util.Map;

import org.ardenus.engine.input.adapter.DeviceAdapter;
import org.ardenus.engine.input.button.PressableState;
import org.ardenus.engine.input.device.DeviceButton;
import org.ardenus.engine.input.device.InputDevice;

/**
 * A controller which and can send receive input data.
 * <p>
 * Examples of controllers include, but are not limited to: XBOX controllers,
 * PlayStation controllers, Nintendo GameCube controllers, etc. By default, a
 * controller has support for buttons and analog sticks. However, depending on
 * the implementation, features like rumble, gyroscopes, etc. may be present.
 * <p>
 * <b>Note:</b> For an controller to work properly, it must be polled via
 * {@link #poll()} before querying any input information. It is recommended to
 * poll the controller once on every application update.
 */
public abstract class Controller extends InputDevice {

	private final Map<Direction, PressableState> directions;
	private final Map<Direction, ControllerButton> directionButtons;

	/**
	 * Constructs a new {@code Controller}.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Controller(DeviceAdapter<?> adapter) {
		super(adapter);

		this.directions = new HashMap<>();
		for (Direction direction : Direction.values()) {
			directions.put(direction, new PressableState());
		}

		/*
		 * It is possible for a button to be added before this map can be
		 * instantiated, thanks to the @ButtonPresent annotation. To remedy
		 * this, each button must be mapped to a direction in the constructor
		 * when map is instantiated. Afterwards, the overridden add button
		 * method will take care of mapping buttons to directions.
		 */
		this.directionButtons = new HashMap<>();
		for (DeviceButton button : this.getButtons()) {
			this.mapDirection(button);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Note:</b> If {@code button} represents a direction, that button will
	 * be mapped to that direction for this controller. However, if a previous
	 * button has been mapped to that same direction, it will be overridden by
	 * {@code button}.
	 */
	@Override
	public void addButton(DeviceButton button) {
		super.addButton(button);
		if (directionButtons != null) {
			this.mapDirection(button);
		}
	}

	private void mapDirection(DeviceButton button) {
		if (button instanceof ControllerButton) {
			ControllerButton cb = (ControllerButton) button;
			if (cb.direction != null) {
				directionButtons.put(cb.direction, cb);
			}
		}
	}

	private boolean pollDirection(Direction direction) {
		ControllerButton button = directionButtons.get(direction);
		if (button != null && this.isPressed(button)) {
			return true;
		}
		/* TODO: add support for analog sticks */
		return false;
	}

	/**
	 * Returns if a direction is currently pressed.
	 * <p>
	 * Whether or not a direction is considered to be pressed is based on the
	 * definition of {@link PressableState#isPressed()}. This method is more or
	 * less a shorthand for it.
	 * 
	 * @param direction
	 *            the direction whose state to check.
	 * @return {@code true} if {@code direction} is currently pressed,
	 *         {@code false} otherwise.
	 */
	public boolean isPressed(Direction direction) {
		if (direction == null) {
			return false;
		}
		return directions.get(direction).isPressed();
	}

	/**
	 * Returns if a direction is currently held down.
	 * <p>
	 * Whether or not a direction is considered to be held down is based on the
	 * definition of {@link PressableState#isHeld()}. This method is more or
	 * less a shorthand for it.
	 * 
	 * @param direction
	 *            the direction whose state to check.
	 * @return {@code true} if {@code direction} is currently held down,
	 *         {@code false} otherwise.
	 */
	public boolean isHeld(Direction direction) {
		if (direction == null) {
			return false;
		}
		return directions.get(direction).isHeld();
	}

	@Override
	public void poll() {
		super.poll();
		for (Direction direction : directions.keySet()) {
			PressableState state = directions.get(direction);
			state.cache();
			state.setPressed(this.pollDirection(direction));
			state.update();
		}
	}

}
