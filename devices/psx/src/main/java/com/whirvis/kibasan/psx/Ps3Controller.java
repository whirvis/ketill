package com.whirvis.kibasan.psx;

import com.whirvis.controller.AnalogTrigger;
import com.whirvis.controller.DeviceButton;
import com.whirvis.controller.RumbleMotor;
import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.DeviceId;
import com.whirvis.kibasan.FeaturePresent;

/**
 * A Sony PlayStation 3 controller.
 */
@DeviceId("ps3")
public class Ps3Controller extends PsxController {

	/* @formatter: off */
	@FeaturePresent
	public static final DeviceButton
			SELECT = new DeviceButton("select"),
			START = new DeviceButton("start");
	
	@FeaturePresent
	public static final AnalogTrigger
			LT = new AnalogTrigger("lt"),
			RT = new AnalogTrigger("rt");
	
	@FeaturePresent
	public static final RumbleMotor
			RUMBLE_STRONG = new RumbleMotor("strong_rumble"),
			RUMBLE_WEAK = new RumbleMotor("weak_rumble");
	
	/* TODO: player LED feature (present in wii-hid branch) */
	/* @formatter: on */
	
	/**
	 * @param adapter
	 *            the PlayStation 3 controller adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Ps3Controller(DeviceAdapter<Ps3Controller> adapter) {
		super(adapter, LT, RT);
	}
	
}
