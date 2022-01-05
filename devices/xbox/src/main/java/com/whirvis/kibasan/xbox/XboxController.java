package com.whirvis.kibasan.xbox;

import com.whirvis.controller.*;
import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.Direction;
import com.whirvis.kibasan.FeaturePresent;

/**
 * A Microsoft XBOX controller.
 */
public class XboxController extends Controller {

	/* @formatter: off */
	@FeaturePresent
	public static final DeviceButton
			A = new DeviceButton("a"),
			B = new DeviceButton("b"),
			X = new DeviceButton("x"),
			Y = new DeviceButton("y"),
			LB = new DeviceButton("lb"),
			RB = new DeviceButton("rb"),
			GUIDE = new DeviceButton("menu"),
			START = new DeviceButton("pause"),
			THUMB_L = new DeviceButton("ls"),
			THUMB_R = new DeviceButton("rs"),
			UP = new DeviceButton("up", Direction.UP),
			RIGHT = new DeviceButton("right", Direction.RIGHT),
			DOWN = new DeviceButton("down", Direction.DOWN),
			LEFT = new DeviceButton("left", Direction.LEFT);

	@FeaturePresent
	public static final AnalogStick
			LS = new AnalogStick("ls", THUMB_L),
			RS = new AnalogStick("rs", THUMB_R);
	
	@FeaturePresent
	public static final AnalogTrigger
			LT = new AnalogTrigger("lt"),
			RT = new AnalogTrigger("rt");
	
	@FeaturePresent
	public static final RumbleMotor
			RUMBLE_COARSE = new RumbleMotor("coarse_rumble"),
			RUMBLE_FINE = new RumbleMotor("fine_rumble");
	/* @formatter: on */

	/**
	 * @param adapter
	 *            the XBOX controller adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public XboxController(DeviceAdapter<XboxController> adapter) {
		super("xbox", adapter, LS, RS, LT, RT);
	}

}
