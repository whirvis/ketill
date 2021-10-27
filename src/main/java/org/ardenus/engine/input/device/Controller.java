package org.ardenus.engine.input.device;

import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.ardenus.engine.input.device.feature.AnalogTrigger;
import org.ardenus.engine.input.device.feature.Button1bc;
import org.ardenus.engine.input.device.feature.DeviceAnalog;
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
 * PlayStation controllers, Nintendo Switch controllers, etc. By default, a
 * controller has support for buttons and analog sticks. Depending on the
 * implementation, features like rumble, gyroscopes, etc. may also be present.
 * <p>
 * <b>Note:</b> For a controller to work properly, it must be polled via
 * {@link #poll()} before querying any input information. It is recommended to
 * poll the controller once on every application update.
 * 
 * @see Keyboard
 * @see Mouse
 */
public abstract class Controller extends InputDevice {

	/**
	 * The left and right analog sticks of the controller.<br>
	 * These may not be present, and as such may be {@code null}.
	 */
	public final AnalogStick ls, rs;

	/**
	 * The left and right analog triggers of the controller.<br>
	 * These may not be present, and as such may be {@code null}.
	 */
	public final AnalogTrigger lt, rt;

	/**
	 * By default, a {@code Controller} expects device features of type
	 * {@link DeviceButton} and {@link DeviceAnalog}. As a result, instances of
	 * {@link DeviceButtonMonitor} and {@link AnalogStickMonitor} are added
	 * automatically on instantiation.
	 * 
	 * @param id
	 *            the controller ID, should be {@code null} if the
	 *            {@link DeviceId} annotation is present for this class.
	 * @param adapter
	 *            the device adapter.
	 * @param ls
	 *            the left analog stick, may be {@code null}.
	 * @param rs
	 *            the right analog stick, may be {@code null}.
	 * @param lt
	 *            the left analog trigger, may be {@code null}.
	 * @param rt
	 *            the right analog trigger, may be {@code null}.
	 * @throws IllegalArgumentException
	 *             if the {@link DeviceId} annotation is present and {@code id}
	 *             is not {@code null}.
	 * @throws NullPointerException
	 *             if no ID was specified for this device; if {@code adapter} is
	 *             {@code null}.
	 */
	public Controller(String id, DeviceAdapter<?> adapter, AnalogStick ls,
			AnalogStick rs, AnalogTrigger lt, AnalogTrigger rt) {
		super(id, adapter);

		this.ls = ls;
		this.rs = rs;
		this.lt = lt;
		this.rt = rt;

		this.addMonitor(new DeviceButtonMonitor(this));
		this.addMonitor(new AnalogStickMonitor(this));
	}

	/**
	 * When using this constructor, the device ID is determined by the
	 * {@link DeviceId} annotation, which must be present for this class.
	 * <p>
	 * By default, a {@code Controller} expects device features of type
	 * {@link DeviceButton} and {@link DeviceAnalog}. As a result, instances of
	 * {@link DeviceButtonMonitor} and {@link AnalogStickMonitor} are added
	 * automatically on instantiation.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @param ls
	 *            the left analog stick, may be {@code null}.
	 * @param rs
	 *            the right analog stick, may be {@code null}.
	 * @param lt
	 *            the left analog trigger, may be {@code null}.
	 * @param rt
	 *            the right analog trigger, may be {@code null}.
	 * @throws NullPointerException
	 *             if no ID was specified for this device; if {@code adapter} is
	 *             {@code null}.
	 */
	public Controller(DeviceAdapter<?> adapter, AnalogStick ls, AnalogStick rs,
			AnalogTrigger lt, AnalogTrigger rt) {
		this(null, adapter, ls, rs, lt, rt);
	}

	/**
	 * @return the current position of the left stick.
	 * @see #getPosition(AnalogStick)
	 */
	public Vector3fc getLsPosition() {
		return this.getPosition(ls);
	}

	/**
	 * @return the current position of the right stick.
	 * @see #getPosition(AnalogStick)
	 */
	public Vector3fc getRsPosition() {
		return this.getPosition(rs);
	}

	/**
	 * @return the current force of the left trigger.
	 * @see #getForce(AnalogTrigger)
	 */
	public float getLtForce() {
		return this.getForce(lt);
	}

	/**
	 * @return the current force of the right trigger.
	 * @see #getForce(AnalogTrigger)
	 */
	public float getRtForce() {
		return this.getForce(rt);
	}

	public boolean isPressed(DeviceButton button) {
		if (!this.hasFeature(button)) {
			return false;
		}
		Button1bc value = this.getState(button);
		return value.pressed();
	}

	public Vector3fc getPosition(AnalogStick stick) {
		if (!this.hasFeature(stick)) {
			return null;
		}
		return this.getState(stick);
	}

	public float getForce(AnalogTrigger trigger) {
		if (!this.hasFeature(trigger)) {
			return 0.0F;
		}
		Trigger1fc value = this.getState(trigger);
		return value.force();
	}

	/**
	 * @param motor
	 *            the motor which to rumble.
	 * @param force
	 *            the vibration force to set the motor to. This value should be
	 *            on a scale of {@code 0.0F} to {@code 1.0F}, with {@code 0.0F}
	 *            being no rumble and {@code 1.0F} being max rumble.
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
	 *            being no rumble and {@code 1.0F} being max rumble.
	 */
	public void setVibration(float force) {
		for (DeviceFeature<?> feature : this.getFeatures()) {
			if (feature instanceof RumbleMotor) {
				this.setVibration((RumbleMotor) feature, force);
			}
		}
	}

}
