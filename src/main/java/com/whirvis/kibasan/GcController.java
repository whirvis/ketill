package com.whirvis.kibasan;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.adapter.DeviceAdapter;
import com.whirvis.kibasan.feature.AnalogStick;
import com.whirvis.kibasan.feature.AnalogTrigger;
import com.whirvis.kibasan.feature.DeviceButton;
import com.whirvis.kibasan.feature.FeaturePresent;
import com.whirvis.kibasan.feature.RumbleMotor;

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
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @param adapter
	 *            the GameCube controller adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public GcController(EventManager events,
			DeviceAdapter<GcController> adapter) {
		super(events, adapter, LS, RS, LT, RT);
	}

}
