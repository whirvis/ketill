package org.ardenus.engine.input.device.controller;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.ardenus.engine.input.device.feature.AnalogTrigger;
import org.ardenus.engine.input.device.feature.FeaturePresent;
import org.ardenus.engine.input.device.feature.RumbleMotor;
import org.joml.Vector3fc;

/**
 * A Microsoft XBOX controller.
 * 
 * @see Controller
 */
public class XboxController extends Controller {

	/* @formatter: off */
	@FeaturePresent
	public static final ControllerButton
			A = new ControllerButton("A"),
			B = new ControllerButton("B"),
			X = new ControllerButton("X"),
			Y = new ControllerButton("Y"),
			LB = new ControllerButton("LB"),
			RB = new ControllerButton("RB"),
			GUIDE = new ControllerButton("Menu"),
			START = new ControllerButton("Pause"),
			THUMB_L = new ControllerButton("LS"),
			THUMB_R = new ControllerButton("RS"),
			UP = new ControllerButton("Up", Direction.UP),
			RIGHT = new ControllerButton("Right", Direction.RIGHT),
			DOWN = new ControllerButton("Down", Direction.DOWN),
			LEFT = new ControllerButton("Left", Direction.LEFT);

	@FeaturePresent
	public static final AnalogTrigger
			LT = new AnalogTrigger("LT"),
			RT = new AnalogTrigger("RT");

	@FeaturePresent
	public static final AnalogStick
			LS = new AnalogStick("LS", THUMB_L),
			RS = new AnalogStick("RS", THUMB_R);
	
	@FeaturePresent
	public static final RumbleMotor
			RUMBLE_COARSE = new RumbleMotor("Coarse"),
			RUMBLE_FINE = new RumbleMotor("Fine");
	/* @formatter: on */

	/**
	 * Constructs a new {@code XboxController}.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public XboxController(DeviceAdapter<XboxController> adapter) {
		super(adapter);
	}
	
	@Override
	public Vector3fc getLeftAnalog() {
		return this.getPosition(LS);
	}
	
	@Override
	public Vector3fc getRightAnalog() {
		return this.getPosition(RS);
	}
	
	@Override
	public float getLeftTrigger() {
		return this.getForce(LT);
	}
	
	@Override
	public float getRightTrigger() {
		return this.getForce(RT);
	}

}
