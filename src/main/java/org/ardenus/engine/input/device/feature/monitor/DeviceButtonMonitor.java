package org.ardenus.engine.input.device.feature.monitor;

import java.util.HashMap;
import java.util.Map;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.feature.DeviceButton;
import org.ardenus.engine.input.device.feature.DeviceFeature;

public class DeviceButtonMonitor extends FeatureMonitor {

	private final Map<DeviceButton, PressableState> states;

	/**
	 * @param device
	 *            the device whose button to monitor.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public DeviceButtonMonitor(InputDevice device) {
		super(device);
		this.states = new HashMap<>();
	}

	@Override
	public void monitor(DeviceFeature<?> feature) {
		if (feature instanceof DeviceButton) {
			DeviceButton button = (DeviceButton) feature;
			states.put(button, new ButtonPressableState(device, button));
		}
	}

	@Override
	public void forget(DeviceFeature<?> feature) {
		states.remove(feature);
	}

	@Override
	public void update() {
		long currentTime = System.currentTimeMillis();
		for (PressableState state : states.values()) {
			state.update(currentTime);
		}
	}

}
