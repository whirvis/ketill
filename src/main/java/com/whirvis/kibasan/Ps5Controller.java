package com.whirvis.kibasan;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.adapter.DeviceAdapter;
import com.whirvis.kibasan.feature.AnalogTrigger;
import com.whirvis.kibasan.feature.DeviceButton;
import com.whirvis.kibasan.feature.FeaturePresent;

/**
 * A Sony PlayStation 5 controller.
 */
@DeviceId("ps5")
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
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @param adapter
	 *            the PlayStation 5 controller adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Ps5Controller(EventManager events, DeviceAdapter<Ps5Controller> adapter) {
		super(events, adapter, LT, RT);
	}
	
}
