package org.ardenus.engine.input.device;

import java.util.HashMap;
import java.util.Map;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.Input;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.event.FeaturePressEvent;
import org.ardenus.engine.input.device.event.FeatureReleaseEvent;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.ardenus.engine.input.device.feature.AnalogTrigger;
import org.ardenus.engine.input.device.feature.Button1bc;
import org.ardenus.engine.input.device.feature.DeviceButton;
import org.ardenus.engine.input.device.feature.DeviceFeature;
import org.ardenus.engine.input.device.feature.RumbleMotor;
import org.ardenus.engine.input.device.feature.Trigger1fc;
import org.ardenus.engine.input.device.feature.Vibration1f;
import org.joml.Vector3fc;

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

	private static final float STICK_PRESS = 2.0F / 3.0F;
	
	private static class PressableState {

		public boolean pressed;
		public long pressTime;
		public boolean held;
		public long lastHeldPress;

	}

	private final Map<DeviceButton, PressableState> buttonStates;
	private final Map<AnalogStick, Map<Direction, PressableState>> stickStates;

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
		this.buttonStates = new HashMap<>();
		this.stickStates = new HashMap<>();
	}

	/**
	 * Returns if a button is pressed.
	 * 
	 * @param button
	 *            the button to check.
	 * @return {@code true} if {@code button} is pressed, {@code false}
	 *         otherwise.
	 */
	public boolean isPressed(DeviceButton button) {
		if (!this.hasFeature(button)) {
			return false;
		}
		Button1bc value = this.getState(button);
		return value.pressed();
	}

	/**
	 * Returns the position of an analog stick.
	 * 
	 * @param stick
	 *            the analog stick.
	 * @return the position of {@code stick}.
	 */
	public Vector3fc getPosition(AnalogStick stick) {
		if (!this.hasFeature(stick)) {
			return null;
		}
		return this.getState(stick);
	}

	/**
	 * Returns if an analog stick is pressed towards a direction.
	 * 
	 * @param stick
	 *            the analog stick.
	 * @param direction
	 *            the direction to check for.
	 * @return {@code true} if {@code stick} is pressed towards
	 *         {@code direction}.
	 */
	public boolean isPressed(AnalogStick stick, Direction direction) {
		Vector3fc pos = this.getPosition(stick);
		if (pos == null) {
			return false;
		}

		switch (direction) {
		case UP:
			return pos.y() >= STICK_PRESS;
		case DOWN:
			return pos.y() <= -STICK_PRESS;
		case LEFT:
			return pos.x() <= -STICK_PRESS;
		case RIGHT:
			return pos.x() >= STICK_PRESS;
		}

		throw new UnsupportedOperationException(
				"unknown direction, this should not occurr");
	}

	/**
	 * Returns the force of an analog trigger.
	 * 
	 * @param trigger
	 *            the analog trigger.
	 * @return the force of {@code trigger}.
	 */
	public float getForce(AnalogTrigger trigger) {
		if (!this.hasFeature(trigger)) {
			return 0.0F;
		}
		Trigger1fc value = this.getState(trigger);
		return value.force();
	}

	/**
	 * Returns the current position of the left analog stick.
	 * 
	 * @return the current position of the left analog stick.
	 */
	public abstract Vector3fc getLeftStick();

	/**
	 * Returns the current position of the right analog stick.
	 * 
	 * @return the current position of the right analog stick.
	 */
	public abstract Vector3fc getRightStick();

	/**
	 * Returns the current force of the left analog trigger.
	 * 
	 * @return the current force of the left analog trigger.
	 */
	public abstract float getLeftTrigger();

	/**
	 * Returns the current force of the right analog trigger.
	 * 
	 * @return the current force of the right analog trigger.
	 */
	public abstract float getRightTrigger();

	/**
	 * Sets the vibration force of a rumble motor.
	 * 
	 * @param motor
	 *            the motor to rumble.
	 * @param force
	 *            the vibration force to set the motor to.
	 */
	public void setVibration(RumbleMotor motor, float force) {
		if (!this.hasFeature(motor)) {
			return;
		}
		Vibration1f vibration = this.getState(motor);
		vibration.force = force;
	}

	/**
	 * Sets the vibration force of all rumble motors.
	 * 
	 * @param force
	 *            the vibration force to set the motors to.
	 */
	public void setVibration(float force) {
		for (DeviceFeature<?> feature : this.getFeatures()) {
			if (feature instanceof RumbleMotor) {
				this.setVibration((RumbleMotor) feature, force);
			}
		}
	}

	private void pollState(long currentTime, PressableState state,
			DeviceFeature<?> feature, Direction direction, boolean pressed) {
		boolean wasPressed = state.pressed;

		if (!wasPressed && pressed) {
			Input.sendEvent(
					new FeaturePressEvent(this, feature, direction, false));
			state.pressTime = currentTime;
		} else if (wasPressed && !pressed) {
			Input.sendEvent(new FeatureReleaseEvent(this, feature, direction,
					state.held));
			state.held = false;
		}

		state.pressed = pressed;
		if (state.pressed) {
			if (!state.held && currentTime - state.pressTime >= 1000L) {
				/* TODO: initial hold event */
				state.held = true;
			}

			if (state.held && currentTime - state.lastHeldPress >= 100L) {
				Input.sendEvent(new FeaturePressEvent(this, feature,
						direction, true));
				state.lastHeldPress = currentTime;
			}
		}
	}

	private void pollButton(long currentTime, DeviceButton button,
			Direction direction) {
		PressableState state = buttonStates.get(button);
		if (state == null) {
			state = new PressableState();
			buttonStates.put(button, state);
		}

		boolean pressed = this.isPressed(button);
		this.pollState(currentTime, state, button, direction, pressed);
	}

	private void pollStick(long currentTime, AnalogStick stick) {
		Map<Direction, PressableState> states = stickStates.get(stick);
		if (states == null) {
			states = new HashMap<>();
			for (Direction direction : Direction.values()) {
				states.put(direction, new PressableState());
			}
			stickStates.put(stick, states);
		}

		for(Direction d : states.keySet()) {
			this.pollState(currentTime, states.get(d), stick, d, this.isPressed(stick, d));
		}
	}

	@Override
	public void poll() {
		long currentTime = System.currentTimeMillis();
		super.poll();

		for (DeviceFeature<?> feature : this.getFeatures()) {
			if (feature instanceof DeviceButton) {
				DeviceButton button = (DeviceButton) feature;
				Direction d = null;
				if (button instanceof ControllerButton) {
					d = ((ControllerButton) button).direction;
				}
				this.pollButton(currentTime, button, d);
			} else if(feature instanceof AnalogStick) {
				this.pollStick(currentTime, (AnalogStick) feature);
			}
		}
	}

}
