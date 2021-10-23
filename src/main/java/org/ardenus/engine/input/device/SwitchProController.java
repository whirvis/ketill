package org.ardenus.engine.input.device;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.ardenus.engine.input.device.feature.DeviceButton;
import org.ardenus.engine.input.device.feature.FeaturePresent;

/**
 * A Nintendo Switch Pro controller.
 */
public class SwitchProController extends Controller {

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
	 * @param adapter
	 *            the Switch controller adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public SwitchProController(DeviceAdapter<SwitchProController> adapter) {
		super("switch_pro", adapter, LS, RS, null, null);
	}

	/**
	 * Since the Nintendo Switch Pro controller has no actual analog triggers,
	 * this method only emulates their functionality. If the {@code ZL} button
	 * is currently pressed, this method will return {@code 1.0F}. Otherwise,
	 * {@code 0.0F} will be returned.
	 */
	@Override
	public float getLtForce() {
		return this.isPressed(ZL) ? 1.0F : 0.0F;
	}

	/**
	 * Since the Nintendo Switch Pro controller has no actual analog triggers,
	 * this method only emulates their functionality. If the {@code ZR} button
	 * is currently pressed, this method will return {@code 1.0F}. Otherwise,
	 * {@code 0.0F} will be returned.
	 */
	@Override
	public float getRtForce() {
		return this.isPressed(ZR) ? 1.0F : 0.0F;
	}

}
