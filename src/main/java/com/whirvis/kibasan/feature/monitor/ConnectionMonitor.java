package com.whirvis.kibasan.feature.monitor;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.event.DeviceConnectEvent;
import com.whirvis.kibasan.event.DeviceDisconnectEvent;
import com.whirvis.kibasan.feature.DeviceFeature;

public class ConnectionMonitor extends FeatureMonitor {

	private boolean wasConnected;

	/**
	 * @param device
	 *            the device whose connection to monitor.
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public ConnectionMonitor(InputDevice device, EventManager events) {
		super(device, events);
	}

	@Override
	public void monitor(DeviceFeature<?> feature) {
		/* nothing to monitor */
	}

	@Override
	public void update() {
		boolean connected = device.isConnected();
		if (!wasConnected && connected) {
			events.send(new DeviceConnectEvent(device));
		} else if (wasConnected && !connected) {
			events.send(new DeviceDisconnectEvent(device));
		}
		this.wasConnected = connected;
	}

}
