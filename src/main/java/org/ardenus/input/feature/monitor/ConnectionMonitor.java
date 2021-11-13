package org.ardenus.input.feature.monitor;

import org.ardenus.input.InputDevice;
import org.ardenus.input.event.DeviceConnectEvent;
import org.ardenus.input.event.DeviceDisconnectEvent;
import org.ardenus.input.feature.DeviceFeature;

import com.whirvex.event.EventManager;

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
