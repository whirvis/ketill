package org.ardenus.engine.input.device;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.ardenus.engine.input.device.feature.AnalogTrigger;
import org.ardenus.engine.input.device.feature.Button1bc;
import org.ardenus.engine.input.device.feature.DeviceButton;
import org.ardenus.engine.input.device.feature.DeviceFeature;
import org.ardenus.engine.input.device.feature.RumbleMotor;
import org.ardenus.engine.input.device.feature.Trigger1fc;
import org.ardenus.engine.input.device.feature.Vibration1f;
import org.ardenus.engine.input.device.feature.monitor.AnalogStickMonitor;
import org.ardenus.engine.input.device.feature.monitor.DeviceButtonMonitor;
import org.joml.Vector3fc;

/**
 * A controller which and can send receive input data.
 * <p>
 * Examples of controllers include, but are not limited to: XBOX controllers,
 * PlayStation controllers, Nintendo GameCube controllers, etc. By default, a
 * controller has support for buttons and analog sticks. Depending on the
 * implementation, features like rumble, gyroscopes, etc. may also be present.
 * <p>
 * <b>Note:</b> For an controller to work properly, it must be polled via
 * {@link #poll()} before querying any input information. It is recommended to
 * poll the controller once on every application update.
 */
public abstract class Controller extends InputDevice {

	/**
	 * By default, a {@code Controller} expects device features of type
	 * {@link DeviceButton} and {@link DeviceAnalog}. As a result, instances of
	 * {@link DeviceButtonMonitor} and {@link AnalogStickMonitor} will be added
	 * on instantiation.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Controller(DeviceAdapter<?> adapter) {
		super(adapter);
		this.addMonitor(new DeviceButtonMonitor(this));
		this.addMonitor(new AnalogStickMonitor(this));
	}

	/**
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
	 * @param stick
	 *            the analog stick.
	 * @param direction
	 *            the direction to check for.
	 * @return {@code true} if {@code stick} is pressed towards
	 *         {@code direction}, {@code false} otherwise.
	 */
	public boolean isPressed(AnalogStick stick, Direction direction) {
		Vector3fc pos = this.getPosition(stick);
		return AnalogStick.isPressed(pos, direction);
	}

	/**
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
	 * @return the current position of the left analog stick.
	 */
	public abstract Vector3fc getLeftStick();

	/**
	 * @return the current position of the right analog stick.
	 */
	public abstract Vector3fc getRightStick();

	/**
	 * @return the current force of the left analog trigger.
	 */
	public abstract float getLeftTrigger();

	/**
	 * @return the current force of the right analog trigger.
	 */
	public abstract float getRightTrigger();

	/**
	 * @param motor
	 *            the motor which to rumble. If not present, this method will be
	 *            a no-op.
	 * @param force
	 *            the vibration force to set the motor to. This value should be
	 *            on a scale of {@code 0.0F} to {@code 1.0F}, with {@code 0.0F}
	 *            being no rumble and {@code 1.0F} being max rumble
	 *            respectively.
	 */
	public void setVibration(RumbleMotor motor, float force) {
		if (!this.hasFeature(motor)) {
			return;
		}
		Vibration1f vibration = this.getState(motor);
		vibration.force = force;
	}

	/**
	 * A shorthand for {@link #setVibration(RumbleMotor, float)} which sets the
	 * vibration for each rumble motor present on this controller.
	 * 
	 * @param force
	 *            the vibration force to set each motor to. This value should be
	 *            on a scale of {@code 0.0F} to {@code 1.0F}, with {@code 0.0F}
	 *            being no rumble and {@code 1.0F} being max rumble
	 *            respectively.
	 */
	public void setVibration(float force) {
		for (DeviceFeature<?> feature : this.getFeatures()) {
			if (feature instanceof RumbleMotor) {
				this.setVibration((RumbleMotor) feature, force);
			}
		}
	}

}
