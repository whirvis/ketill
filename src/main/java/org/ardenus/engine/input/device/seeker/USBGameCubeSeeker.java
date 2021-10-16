package org.ardenus.engine.input.device.seeker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.usb.UsbDevice;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;

import org.ardenus.engine.input.device.GameCubeController;
import org.ardenus.engine.input.device.adapter.gamecube.USBGameCubeControllerAdapter;
import org.ardenus.engine.input.device.adapter.gamecube.USBGameCubeDevice;

public class USBGameCubeSeeker extends DeviceSeeker {

	private static List<UsbDevice> findDevices(UsbHub hub) {
		List<UsbDevice> devices = new ArrayList<>();
		for (Object obj : hub.getAttachedUsbDevices()) {
			UsbDevice device = (UsbDevice) obj;
			if (device.isUsbHub()) {
				devices.addAll(findDevices((UsbHub) device));
			} else {
				devices.add(device);
			}
		}
		return devices;
	}

	private static List<UsbDevice> findDevices() throws UsbException {
		UsbServices services = UsbHostManager.getUsbServices();
		return findDevices(services.getRootUsbHub());
	}

	private final boolean allowMultiple;
	private final List<UsbDevice> troubled;
	private final Map<UsbDevice, USBGameCubeDevice> devices;
	private final Map<USBGameCubeControllerAdapter, GameCubeController> controllers;
	private long lastSearch;

	/**
	 * @param allowMultiple
	 *            {@code true} if multiple USB GameCube adapters should be
	 *            recognized, {@code false} if only the first one found should
	 *            be used.
	 */
	public USBGameCubeSeeker(boolean allowMultiple) {
		super(GameCubeController.class);
		this.allowMultiple = allowMultiple;
		this.troubled = new ArrayList<>();
		this.devices = new HashMap<>();
		this.controllers = new HashMap<>();
	}

	/**
	 * Constructs a new {@code USBGameCubeSeeker} with support for multiple USB
	 * GameCube adapters enabled.
	 */
	public USBGameCubeSeeker() {
		this(true);
	}

	private void findAdapters() throws UsbException {
		for (UsbDevice device : findDevices()) {
			if (troubled.contains(device) || devices.containsKey(device)
					|| !USBGameCubeDevice.isAdapter(device)) {
				continue;
			}

			if (devices.isEmpty() || allowMultiple) {
				USBGameCubeDevice adapter = new USBGameCubeDevice(device);
				devices.put(device, adapter);
			}
		}
	}

	@Override
	protected void seek() throws UsbException {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastSearch >= 100L) {
			this.findAdapters();
			this.lastSearch = currentTime;
		}

		Iterator<UsbDevice> devicesI = devices.keySet().iterator();
		while (devicesI.hasNext()) {
			UsbDevice usb = devicesI.next();
			USBGameCubeDevice device = devices.get(usb);
			try {
				device.poll();
			} catch (UsbDisconnectedException e) {
				devicesI.remove();
				continue;
			} catch (UsbException e) {
				troubled.add(usb);
				devicesI.remove();
				continue;
			}

			for (USBGameCubeControllerAdapter adapter : device.getAdapters()) {
				boolean connected = adapter.isConnected();
				boolean registered = controllers.containsKey(adapter);
				if (connected && !registered) {
					GameCubeController controller =
							new GameCubeController(adapter);
					controllers.put(adapter, controller);
					this.register(controller);
				} else if (!connected && registered) {
					GameCubeController controller = controllers.remove(adapter);
					this.unregister(controller);
				}
			}
		}

	}

}
