package com.whirvis.kibasan;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.adapter.DeviceAdapter;
import com.whirvis.kibasan.feature.AnalogTrigger;
import com.whirvis.kibasan.feature.DeviceButton;
import com.whirvis.kibasan.feature.FeaturePresent;
import com.whirvis.kibasan.feature.RumbleMotor;

public class Ps3Controller extends PsxController {

	/* @formatter: off */
	@FeaturePresent
	public static final DeviceButton
			START = new DeviceButton("start"),
			SELECT = new DeviceButton("select");
			/* TODO: add missing buttons, if any */
	
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
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @param adapter
	 *            the PlayStation 3 controller adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Ps3Controller(EventManager events,
			DeviceAdapter<Ps3Controller> adapter) {
		super(events, adapter, LT, RT);
	}
	
}
