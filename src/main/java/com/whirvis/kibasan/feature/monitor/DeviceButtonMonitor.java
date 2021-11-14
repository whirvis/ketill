package com.whirvis.kibasan.feature.monitor;

import java.util.HashMap;
import java.util.Map;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.feature.DeviceButton;
import com.whirvis.kibasan.feature.DeviceFeature;

public class DeviceButtonMonitor extends FeatureMonitor {

	private final Map<DeviceButton, PressableState> states;

	/**
	 * @param device
	 *            the device whose buttons to monitor.
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public DeviceButtonMonitor(InputDevice device, EventManager events) {
		super(device, events);
		this.states = new HashMap<>();
	}

	@Override
	public void monitor(DeviceFeature<?> feature) {
		if (feature instanceof DeviceButton) {
			DeviceButton button = (DeviceButton) feature;
			states.put(button, new DeviceButtonState(device, events, button));
		}
	}

	@Override
	public void update() {
		long currentTime = System.currentTimeMillis();
		for (PressableState state : states.values()) {
			state.update(currentTime);
		}
	}

}
