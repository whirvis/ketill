package com.whirvis.kibasan;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.adapter.DeviceAdapter;
import com.whirvis.kibasan.feature.DeviceButton;
import com.whirvis.kibasan.feature.FeaturePresent;
import com.whirvis.kibasan.feature.RumbleMotor;

/**
 * A Nintendo Wii controller.
 */
@DeviceId("wii")
public class Wiimote extends Controller {

	/* @formatter: off */
	@FeaturePresent
	public static final DeviceButton
			LEFT = new DeviceButton("left", Direction.LEFT),
			RIGHT = new DeviceButton("right", Direction.RIGHT),
			DOWN = new DeviceButton("down", Direction.DOWN),
			UP = new DeviceButton("up", Direction.UP),
			PLUS = new DeviceButton("plus"),	
			TWO = new DeviceButton("two"),
			ONE = new DeviceButton("one"),
			B = new DeviceButton("b"),
			A = new DeviceButton("a"),
			MINUS = new DeviceButton("minus"),
			HOME = new DeviceButton("home");
	
	@FeaturePresent
	public static final RumbleMotor
			RUMBLE = new RumbleMotor("rumble");
	/* @formatter: on */

	/**
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @param adapter
	 *            the Wiimote adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Wiimote(EventManager events, DeviceAdapter<Wiimote> adapter) {
		super(events, adapter, null, null, null, null);
	}

}
