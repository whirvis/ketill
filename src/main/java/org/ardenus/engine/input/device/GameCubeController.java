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
			A = new DeviceButton("a"),
			B = new DeviceButton("b"),
			X = new DeviceButton("x"),
			Y = new DeviceButton("y"),
			LEFT = new DeviceButton("left", Direction.LEFT),
			RIGHT = new DeviceButton("right", Direction.RIGHT),
			DOWN = new DeviceButton("down", Direction.DOWN),
			UP = new DeviceButton("up", Direction.UP),
			START = new DeviceButton("start"),
			Z = new DeviceButton("z"),
			R = new DeviceButton("r"),
			L = new DeviceButton("l");

	@FeaturePresent
	public static final AnalogStick
			LS = new AnalogStick("ls"),
			RS = new AnalogStick("rs");
	
	@FeaturePresent
	public static final AnalogTrigger
			LT = new AnalogTrigger("lt"),
			RT = new AnalogTrigger("rt");
	
	@FeaturePresent
	public static final RumbleMotor
			RUMBLE = new RumbleMotor("rumble");
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
