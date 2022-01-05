package com.whirvis.kibasan.psx;

import com.whirvis.controller.AnalogTrigger;
import com.whirvis.controller.DeviceButton;
import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.FeaturePresent;

/**
 * A Sony PlayStation 5 controller.
 */
public class Ps5Controller extends PsxController {

	/* @formatter: off */
	@FeaturePresent
	public static final DeviceButton
			SHARE = new DeviceButton("share"),
			OPTIONS = new DeviceButton("options"),
			PS = new DeviceButton("playstation"),
			TPAD = new DeviceButton("trackpad"),
			MUTE = new DeviceButton("mute");
	
	@FeaturePresent
	public static final AnalogTrigger
			LT = new AnalogTrigger("lt"),
			RT = new AnalogTrigger("rt");
	/* @formatter: on */
	
	/**
	 * @param adapter
	 *            the PlayStation 5 controller adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Ps5Controller(DeviceAdapter<Ps5Controller> adapter) {
		super("ps5", adapter, LT, RT);
	}
	
}
