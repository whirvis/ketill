package org.ardenus.engine.input.device;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.ardenus.engine.input.device.feature.FeaturePresent;
import org.joml.Vector3fc;

/**
 * A Nintendo Switch controller.
 * 
 * @see Controller
 */
public class SwitchController extends Controller {

	/* @formatter: off */
	@FeaturePresent
	public static final ControllerButton
			B = new ControllerButton("B"),
			A = new ControllerButton("A"),
			Y = new ControllerButton("Y"),
			X = new ControllerButton("X"),
			L = new ControllerButton("L"),
			R = new ControllerButton("R"),
			ZL = new ControllerButton("ZL"),
			ZR = new ControllerButton("ZR"),
			MINUS = new ControllerButton("-"),
			PLUS = new ControllerButton("+"),
			THUMB_L = new ControllerButton("LS"),
			THUMB_R = new ControllerButton("RS"),
			HOME = new ControllerButton("Home"),
			SCREENSHOT = new ControllerButton("Screenshot"),
			BUMPER = new ControllerButton("Bumper"),
			Z_BUMPER = new ControllerButton("Z Bumper"),
			UP = new ControllerButton("Up", Direction.UP),
			RIGHT = new ControllerButton("Right", Direction.RIGHT),
			DOWN = new ControllerButton("Down", Direction.DOWN),
			LEFT = new ControllerButton("Left", Direction.LEFT);

	@FeaturePresent
	public static final AnalogStick
			LS = new AnalogStick("LS", THUMB_L),
			RS = new AnalogStick("RS", THUMB_R);
	/* @formatter: on */

	/**
	 * Constructs a new {@code SwitchController}.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public SwitchController(DeviceAdapter<SwitchController> adapter) {
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
		return this.isPressed(ZL) ? 1.0F : 0.0F;
	}

	@Override
	public float getRightTrigger() {
		return this.isPressed(ZR) ? 1.0F : 0.0F;
	}

}
