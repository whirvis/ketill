package org.ardenus.input.seeker;

import java.util.HashMap;
import java.util.Map;

import javax.usb.UsbDevice;
import javax.usb.UsbException;

import org.ardenus.input.GcController;
import org.ardenus.input.adapter.gamecube.GcUsbAdapter;
import org.ardenus.input.adapter.gamecube.GcUsbDevice;

import com.whirvex.event.EventManager;

public class UsbGcSeeker extends UsbDeviceSeeker {

	private final boolean allowMultiple;
	private final Map<UsbDevice, GcUsbDevice> hubs;
	private final Map<GcUsbAdapter, GcController> controllers;

	/**
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @param allowMultiple
	 *            {@code true} if multiple USB GameCube adapters should be
	 *            recognized, {@code false} if only the first one found should
	 *            be used.
	 */
	public UsbGcSeeker(EventManager events, boolean allowMultiple) {
		super(GcController.class, events);

		this.allowMultiple = allowMultiple;
		this.hubs = new HashMap<>();
		this.controllers = new HashMap<>();

		this.seekDevice(GcUsbDevice.VENDOR_ID, GcUsbDevice.PRODUCT_ID);
	}

	/**
	 * Constructs a new {@code UsbGcSeeker} with support for multiple USB
	 * GameCube adapters enabled.
	 * 
	 * @param events
	 *            the event manager, may be {@code null}.
	 */
	public UsbGcSeeker(EventManager events) {
		this(events, true);
	}

	@Override
	protected void onAttach(UsbDevice device) {
		if (hubs.isEmpty() || allowMultiple) {
			hubs.put(device, new GcUsbDevice(device));
		}
	}

	@Override
	protected void onDetach(UsbDevice device) {
		hubs.remove(device);
	}

	@Override
	protected void poll(UsbDevice device) throws UsbException {
		GcUsbDevice hub = hubs.get(device);
		if (hub == null) {
			return;
		}

		hub.poll();
		for (GcUsbAdapter adapter : hub.getAdapters()) {
			boolean connected = adapter.isConnected();
			boolean registered = controllers.containsKey(adapter);
			if (connected && !registered) {
				GcController controller = new GcController(events, adapter);
				controllers.put(adapter, controller);
				this.register(controller);
			} else if (!connected && registered) {
				GcController controller = controllers.remove(adapter);
				this.unregister(controller);
			}
		}
	}

}
