package org.ardenus.engine.input.device.feature.monitor;

import org.ardenus.engine.input.Input;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.event.DeviceConnectEvent;
import org.ardenus.engine.input.device.event.DeviceDisconnectEvent;
import org.ardenus.engine.input.device.feature.DeviceFeature;

/**
 * A feature monitor which tracks the connection state of an
 * {@link InputDevice}.
 */
public class ConnectionMonitor extends FeatureMonitor {

	private boolean wasConnected;

	/**
	 * Constructs a new {@code ConnectionMonitor}.
	 * 
	 * @param device
	 *            the device whose connection to monitor.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public ConnectionMonitor(InputDevice device) {
		super(device);
	}

	@Override
	public void monitor(DeviceFeature<?> feature) {
		/* nothing to monitor */
	}

	@Override
	public void forget(DeviceFeature<?> feature) {
		/* nothing to forget */
	}

	@Override
	public void update() {
		boolean connected = device.isConnected();
		if (!wasConnected && connected) {
			Input.sendEvent(new DeviceConnectEvent(device));
		} else if (wasConnected && !connected) {
			Input.sendEvent(new DeviceDisconnectEvent(device));
		}
		this.wasConnected = connected;
	}

}
