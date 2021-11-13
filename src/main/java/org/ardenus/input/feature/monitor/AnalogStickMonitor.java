package org.ardenus.input.feature.monitor;

import java.util.HashSet;
import java.util.Set;

import org.ardenus.input.Direction;
import org.ardenus.input.InputDevice;
import org.ardenus.input.feature.AnalogStick;
import org.ardenus.input.feature.DeviceFeature;

import com.whirvex.event.EventManager;

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
