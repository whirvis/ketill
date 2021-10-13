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
			B = new DeviceButton("b"),
			A = new DeviceButton("a"),
			Y = new DeviceButton("y"),
			X = new DeviceButton("x"),
			L = new DeviceButton("l"),
			R = new DeviceButton("r"),
			ZL = new DeviceButton("zl"),
			ZR = new DeviceButton("zr"),
			MINUS = new DeviceButton("minus"),
			PLUS = new DeviceButton("plus"),
			THUMB_L = new DeviceButton("ls"),
			THUMB_R = new DeviceButton("rs"),
			HOME = new DeviceButton("home"),
			SCREENSHOT = new DeviceButton("screenshot"),
			BUMPER = new DeviceButton("bumper"),
			Z_BUMPER = new DeviceButton("z_bumper"),
			UP = new DeviceButton("up", Direction.UP),
			RIGHT = new DeviceButton("right", Direction.RIGHT),
			DOWN = new DeviceButton("down", Direction.DOWN),
			LEFT = new DeviceButton("left", Direction.LEFT);

	@FeaturePresent
	public static final AnalogStick
			LS = new AnalogStick("ls", THUMB_L),
			RS = new AnalogStick("rs", THUMB_R);
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
