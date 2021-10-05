package org.ardenus.engine.input.device.controller;

import org.ardenus.engine.input.device.DeviceFeature;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.analog.AnalogStick;
import org.ardenus.engine.input.device.analog.AnalogTrigger;
import org.ardenus.engine.input.device.analog.Trigger1fc;
import org.ardenus.engine.input.device.button.Button1bc;
import org.ardenus.engine.input.device.button.DeviceButton;
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
	public abstract Vector3fc getLeftAnalog();

	/**
	 * Returns the current position of the right analog stick.
	 * 
	 * @return the current position of the right analog stick.
	 */
	public abstract Vector3fc getRightAnalog();

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

}
