package com.whirvis.kibasan.gc;

import com.whirvis.controller.*;
import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.DeviceId;
import com.whirvis.kibasan.Direction;
import com.whirvis.kibasan.FeaturePresent;

/**
 * A Nintendo GameCube controller.
 */
@DeviceId("gc")
public class GcController extends Controller {

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
	 * @param adapter
	 *            the GameCube controller adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public GcController(DeviceAdapter<GcController> adapter) {
		super(adapter, LS, RS, LT, RT);
	}

}
