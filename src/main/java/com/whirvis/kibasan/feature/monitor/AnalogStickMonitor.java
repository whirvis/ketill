package com.whirvis.kibasan.feature.monitor;

import java.util.HashSet;
import java.util.Set;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.Direction;
import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.feature.AnalogStick;
import com.whirvis.kibasan.feature.DeviceFeature;

public class AnalogStickMonitor extends FeatureMonitor {

	private final Set<AnalogStickState> states;

	/**
	 * @param device
	 *            the device whose sticks to monitor.
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public AnalogStickMonitor(InputDevice device, EventManager events) {
		super(device, events);
		this.states = new HashSet<>();
	}

	@Override
	public void monitor(DeviceFeature<?> feature) {
		if (feature instanceof AnalogStick) {
			AnalogStick stick = (AnalogStick) feature;
			for (Direction direction : Direction.values()) {
				states.add(
						new AnalogStickState(device, events, stick, direction));
			}
		}
	}

	@Override
	public void update() {
		long currentTime = System.currentTimeMillis();
		for (PressableState stick : states) {
			stick.update(currentTime);
		}
	}

}
