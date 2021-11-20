package com.whirvis.kibasan.seeker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.HidServicesSpecification;
import org.hid4java.event.HidServicesEvent;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.InputException;

public abstract class HidDeviceSeeker extends DeviceSeeker
		implements HidServicesListener {

	private static String getSerialStr(HidDevice device) {
		String serial = device.getSerialNumber();
		if (serial != null) {
			return "serial number " + serial;
		}
		return "unknown serial number";
	}

	private final Set<DeviceDesc> descs;
	private final Set<HidDevice> devices;
	private final Set<HidDevice> troubled;
	private final HidServices services;
	private boolean startedServices;

	/*
	 * Because the event methods drop any exceptions thrown into the void, all
	 * of their code is wrapped into a try catch all block. If they experience
	 * any exceptions, they will store them into this variable. Afterwards, the
	 * seek() function will throw it on behalf of the listener functions.
	 */
	private Exception hidException;

	/**
	 * @param type
	 *            the input device type.
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}.
	 * @see #seekDevice(int, int)
	 */
	public HidDeviceSeeker(Class<? extends InputDevice> type,
			EventManager events) {
		super(type, events);

		this.descs = new HashSet<>();
		this.devices = new HashSet<>();
		this.troubled = new HashSet<>();

		HidServicesSpecification specs = new HidServicesSpecification();
		specs.setAutoStart(false);
		specs.setScanInterval(1000);
		specs.setPauseInterval(0);
		this.services = HidManager.getHidServices(specs);
		services.addHidServicesListener(this);
	}

	public boolean isSeeking(int vendorId, int productId) {
		for (DeviceDesc desc : descs) {
			if (desc.vendorId == vendorId && desc.productId == productId) {
				return true;
			}
		}
		return false;
	}

	private boolean isSeeking(HidDevice device) {
		int vendorId = device.getVendorId();
		int productId = device.getProductId();
		return this.isSeeking(vendorId, productId);
	}

	/**
	 * When detected, the vendor and product ID of an HID device will be checked
	 * to see if the seeker should connect it. This is to prevent undesired HID
	 * devices from being erroneously connected.
	 * 
	 * @param vendorId
	 *            the vendor ID.
	 * @param productId
	 *            the product ID.
	 */
	protected void seekDevice(int vendorId, int productId) {
		if (this.isSeeking(vendorId, productId)) {
			return;
		}
		descs.add(new DeviceDesc(vendorId, productId));
		String idStr = DeviceDesc.getStr(vendorId, productId);
		log.debug("Seeking devices with ID " + idStr);
	}

	/**
	 * All currently attached HID devices with a matching vendor and product ID
	 * will be automatically disconnected. This is to prevent the connection of
	 * undesired HID devices from lingering.
	 * 
	 * @param vendorId
	 *            the vendor ID.
	 * @param productId
	 *            ID the product ID.
	 */
	protected void dropDevice(int vendorId, int productId) {
		if (!this.isSeeking(vendorId, productId)) {
			return;
		}

		int count = 0;
		Iterator<HidDevice> devicesI = devices.iterator();
		while (devicesI.hasNext()) {
			HidDevice device = devicesI.next();
			if (this.isSeeking(device)) {
				devicesI.remove();
				this.disconnect(device);
				count++;
			}
		}

		String idStr = DeviceDesc.getStr(vendorId, productId);
		log.debug("Dropped " + count + " devices with ID " + idStr);
	}

	protected abstract void onConnect(HidDevice device);

	private void connect(HidDevice device) {
		if (devices.contains(device)) {
			return;
		}

		device.open();
		device.setNonBlocking(true);
		this.onConnect(device);
		devices.add(device);

		String serialStr = getSerialStr(device);
		log.trace("Device with " + serialStr + " connected");
	}

	protected abstract void onDisconnect(HidDevice device);

	private void disconnect(HidDevice device) {
		if (!devices.contains(device)) {
			return;
		}

		device.close();
		this.onDisconnect(device);
		devices.remove(device);

		String serialStr = getSerialStr(device);
		log.trace("Device with " + serialStr + " disconnected");
	}
	
	protected abstract void onTrouble(HidDevice device, Throwable cause);

	private void markTroubled(HidDevice device, Throwable cause) {
		if (troubled.contains(device)) {
			throw new IllegalStateException("already troubled");
		}
		
		this.onTrouble(device, cause);
		troubled.add(device);
		this.disconnect(device);

		String serialStr = getSerialStr(device);
		log.error("Permanently disconnected device " + serialStr
				+ " due to unhandled issue", cause);
	}

	@Override
	public final void hidDeviceAttached(HidServicesEvent event) {
		try {
			HidDevice device = event.getHidDevice();
			if (devices.contains(device)) {
				return;
			}

			/*
			 * If a device is marked as "troubled", that means it was once
			 * registered once but got disconnected due to an error. To prevent
			 * a continuous loop of connecting devices, encountering an error,
			 * and then disconnecting them again, troubled devices are ignored
			 * once they are marked.
			 */
			if (troubled.contains(device)) {
				return;
			}

			if (this.isSeeking(device)) {
				this.connect(device);
			}
		} catch (Exception e) {
			this.hidException = e;
		}
	}

	@Override
	public final void hidDeviceDetached(HidServicesEvent event) {
		try {
			this.disconnect(event.getHidDevice());
		} catch (Exception e) {
			this.hidException = e;
		}
	}

	@Override
	public final void hidFailure(HidServicesEvent event) {
		try {
			this.markTroubled(event.getHidDevice(), null);
		} catch (Exception e) {
			this.hidException = e;
		}
	}

	/**
	 * @throws InputException
	 *             if no targeted HID devices were specified.
	 * @throws Exception
	 *             if an HID error has occurred.
	 * @see #seekDevice(int, int)
	 */
	@Override
	protected void seek() throws Exception {
		if (descs.isEmpty()) {
			throw new InputException("no HID devices specified");
		} else if (hidException != null) {
			throw hidException;
		}

		if (!startedServices) {
			services.start();
			this.startedServices = true;
			log.debug("Started HID services");
		}
	}

	/**
	 * This method is called for each HID device that is currently registered to
	 * this seeker. If an exception is thrown, the seeker will mark the HID
	 * device to be "troubled", and automatically disconnect it. Afterwards, it
	 * will not be reconnected.
	 * 
	 * @param device
	 *            the HID device being polled.
	 * @throws Exception
	 *             if an error occurs.
	 */
	protected abstract void poll(HidDevice device) throws Exception;

	@Override
	public void poll() {
		super.poll();

		Iterator<HidDevice> devicesI = devices.iterator();
		while (devicesI.hasNext()) {
			HidDevice device = devicesI.next();
			try {
				this.poll(device);
			} catch (Exception e) {
				devicesI.remove();
				this.markTroubled(device, e);
			}
		}
	}

}
