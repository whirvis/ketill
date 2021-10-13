package org.ardenus.engine.input.device;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.ardenus.engine.input.device.feature.DeviceButton;
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
	public static final DeviceButton
			B = new DeviceButton("B"),
			A = new DeviceButton("A"),
			Y = new DeviceButton("Y"),
			X = new DeviceButton("X"),
			L = new DeviceButton("L"),
			R = new DeviceButton("R"),
			ZL = new DeviceButton("ZL"),
			ZR = new DeviceButton("ZR"),
			MINUS = new DeviceButton("-"),
			PLUS = new DeviceButton("+"),
			THUMB_L = new DeviceButton("LS"),
			THUMB_R = new DeviceButton("RS"),
			HOME = new DeviceButton("Home"),
			SCREENSHOT = new DeviceButton("Screenshot"),
			BUMPER = new DeviceButton("Bumper"),
			Z_BUMPER = new DeviceButton("Z Bumper"),
			UP = new DeviceButton("Up", Direction.UP),
			RIGHT = new DeviceButton("Right", Direction.RIGHT),
			DOWN = new DeviceButton("Down", Direction.DOWN),
			LEFT = new DeviceButton("Left", Direction.LEFT);

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
