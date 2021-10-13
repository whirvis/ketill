package org.ardenus.engine.input.device;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.ardenus.engine.input.device.feature.AnalogTrigger;
import org.ardenus.engine.input.device.feature.DeviceButton;
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
	public static final DeviceButton
			SQUARE = new DeviceButton("Square"),
			CROSS = new DeviceButton("Cross"),
			CIRCLE = new DeviceButton("Circle"),
			TRIANGLE = new DeviceButton("Triangle"),
			L1 = new DeviceButton("L1"),
			R1 = new DeviceButton("R1"),
			L2 = new DeviceButton("L2"),
			R2 = new DeviceButton("R2"),
			SHARE = new DeviceButton("Share"),
			OPTIONS = new DeviceButton("Options"),
			THUMB_L = new DeviceButton("LS"),
			THUMB_R = new DeviceButton("RS"),
			PS = new DeviceButton("PlayStation"),
			TPAD = new DeviceButton("Trackpad"),
			MUTE = new DeviceButton("Mute"), /* present on PS5 */
			UP = new DeviceButton("Up", Direction.UP),
			RIGHT = new DeviceButton("Right", Direction.RIGHT),
			DOWN = new DeviceButton("Down", Direction.DOWN),
			LEFT = new DeviceButton("Left", Direction.LEFT);
	
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
