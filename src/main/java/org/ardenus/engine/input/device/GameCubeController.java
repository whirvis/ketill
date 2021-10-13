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
 * A Nintendo GameCube controller.
 *
 * @see Controller
 */
public class GameCubeController extends Controller {

	/* @formatter: off */
	@FeaturePresent
	public static final DeviceButton
			A = new DeviceButton("A"),
			B = new DeviceButton("B"),
			X = new DeviceButton("X"),
			Y = new DeviceButton("Y"),
			LEFT = new DeviceButton("Left", Direction.LEFT),
			RIGHT = new DeviceButton("Right", Direction.RIGHT),
			DOWN = new DeviceButton("Down", Direction.DOWN),
			UP = new DeviceButton("Up", Direction.UP),
			START = new DeviceButton("Start"),
			Z = new DeviceButton("Z"),
			R = new DeviceButton("R"),
			L = new DeviceButton("L");

	@FeaturePresent
	public static final AnalogStick
			LS = new AnalogStick("LS"),
			RS = new AnalogStick("RS");
	
	@FeaturePresent
	public static final AnalogTrigger
			LT = new AnalogTrigger("LT"),
			RT = new AnalogTrigger("RT");
	
	@FeaturePresent
	public static final RumbleMotor
			RUMBLE = new RumbleMotor("Rumble");
	/* @formatter: on */

	/**
	 * Constructs a new {@code GamecubeController}.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public GameCubeController(DeviceAdapter<GameCubeController> adapter) {
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
