package org.ardenus.engine.input.device;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.ardenus.engine.input.device.feature.AnalogTrigger;
import org.ardenus.engine.input.device.feature.FeaturePresent;
import org.joml.Vector3fc;

/**
 * A Sony PlayStation 4 controller.
 * 
 * @see Controller
 */
public class PlayStationController extends Controller {

	/* @formatter: off */
	@FeaturePresent
	public static final ControllerButton
			SQUARE = new ControllerButton("Square"),
			CROSS = new ControllerButton("Cross"),
			CIRCLE = new ControllerButton("Circle"),
			TRIANGLE = new ControllerButton("Triangle"),
			L1 = new ControllerButton("L1"),
			R1 = new ControllerButton("R1"),
			L2 = new ControllerButton("L2"),
			R2 = new ControllerButton("R2"),
			SHARE = new ControllerButton("Share"),
			OPTIONS = new ControllerButton("Options"),
			THUMB_L = new ControllerButton("LS"),
			THUMB_R = new ControllerButton("RS"),
			PS = new ControllerButton("PlayStation"),
			TPAD = new ControllerButton("Trackpad"),
			UP = new ControllerButton("Up", Direction.UP),
			RIGHT = new ControllerButton("Right", Direction.RIGHT),
			DOWN = new ControllerButton("Down", Direction.DOWN),
			LEFT = new ControllerButton("Left", Direction.LEFT);
	
	@FeaturePresent
	public static final AnalogStick
			LS = new AnalogStick("LS", THUMB_L),
			RS = new AnalogStick("RS", THUMB_R);
	
	@FeaturePresent
	public static final AnalogTrigger
			LT = new AnalogTrigger("LT"),
			RT = new AnalogTrigger("RT");
	/* @formatter: on */

	/**
	 * Constructs a new {@code PlayStationController}.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public PlayStationController(DeviceAdapter<PlayStationController> adapter) {
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
