package org.ardenus.engine.input.device.feature.monitor;

import java.util.HashSet;
import java.util.Set;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.ardenus.engine.input.device.feature.DeviceFeature;

public class AnalogStickMonitor extends FeatureMonitor {

	private final Set<AnalogStickState> states;

	/**
	 * @param device
	 *            the device whose sticks to monitor.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public AnalogStickMonitor(InputDevice device) {
		super(device);
		this.states = new HashSet<>();
	}

	@Override
	public void monitor(DeviceFeature<?> feature) {
		if (feature instanceof AnalogStick) {
			AnalogStick stick = (AnalogStick) feature;
			for (Direction direction : Direction.values()) {
				states.add(new AnalogStickState(device, stick, direction));
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
