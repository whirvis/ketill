package org.ardenus.engine.input.device;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.ardenus.engine.input.device.feature.AnalogTrigger;
import org.ardenus.engine.input.device.feature.DeviceButton;
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
	public static final DeviceButton
			A = new DeviceButton("A"),
			B = new DeviceButton("B"),
			X = new DeviceButton("X"),
			Y = new DeviceButton("Y"),
			LB = new DeviceButton("LB"),
			RB = new DeviceButton("RB"),
			GUIDE = new DeviceButton("Menu"),
			START = new DeviceButton("Pause"),
			THUMB_L = new DeviceButton("LS"),
			THUMB_R = new DeviceButton("RS"),
			UP = new DeviceButton("Up", Direction.UP),
			RIGHT = new DeviceButton("Right", Direction.RIGHT),
			DOWN = new DeviceButton("Down", Direction.DOWN),
			LEFT = new DeviceButton("Left", Direction.LEFT);

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
	public Vector3fc getLeftStick() {
		return this.getPosition(LS);
	}
	
	@Override
	public Vector3fc getRightStick() {
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
