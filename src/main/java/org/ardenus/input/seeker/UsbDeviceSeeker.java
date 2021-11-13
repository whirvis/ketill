package org.ardenus.input.seeker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;
import javax.usb.event.UsbDeviceDataEvent;
import javax.usb.event.UsbDeviceErrorEvent;
import javax.usb.event.UsbDeviceEvent;
import javax.usb.event.UsbDeviceListener;

import org.ardenus.input.InputDevice;
import org.ardenus.input.InputException;

import com.whirvex.event.EventManager;

public abstract class UsbDeviceSeeker extends DeviceSeeker
		implements UsbDeviceListener {

	private static final long SEARCH_RATE = 1000L;

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

	private static String getSerialStr(UsbDevice device) {
		try {
			String serial = device.getSerialNumberString();
			if (serial != null) {
				return "serial number " + serial;
			}
		} catch (Exception e) {
			/* don't care, honestly */
		}
		return "unkonwn serial number";
	}

	private final Set<DeviceDesc> descs;
	private final Set<UsbDevice> devices;
	private final Set<UsbDevice> troubled;
	private long lastSearch;

	/**
	 * @param type
	 *            the input device type.
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}.
	 * @see #seekDevice(int, int)
	 */
	public UsbDeviceSeeker(Class<? extends InputDevice> type,
			EventManager events) {
		super(type, events);
		this.descs = new HashSet<>();
		this.devices = new HashSet<>();
		this.troubled = new HashSet<>();
	}

	public boolean isSeeking(int vendorId, int productId) {
		for (DeviceDesc desc : descs) {
			if (desc.vendorId == vendorId && desc.productId == productId) {
				return true;
			}
		}
		return false;
	}

	private boolean isSeeking(UsbDevice device) {
		if (device == null) {
			return false;
		}
		UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
		int vendorId = desc.idVendor();
		int productId = desc.idProduct();
		return this.isSeeking(vendorId, productId);
	}

	/**
	 * When detected, the vendor and product ID of a USB device will be checked
	 * to see if the seeker should attach it. This is to prevent undesired USB
	 * devices from being erroneously attached.
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
	 * All currently attached USB devices with a matching vendor and product ID
	 * will be automatically detached. This is to prevent the connection of
	 * undesired USB devices from lingering.
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
		Iterator<UsbDevice> devicesI = devices.iterator();
		while (devicesI.hasNext()) {
			UsbDevice device = devicesI.next();
			if (this.isSeeking(device)) {
				devicesI.remove();
				this.detach(device);
				count++;
			}
		}

		String idStr = DeviceDesc.getStr(vendorId, productId);
		log.debug("Dropped " + count + " devices with ID " + idStr);
	}

	protected abstract void onAttach(UsbDevice device);

	private void attach(UsbDevice device) {
		if (devices.contains(device)) {
			return;
		}

		device.addUsbDeviceListener(this);
		this.onAttach(device);
		devices.add(device);

		String serialStr = getSerialStr(device);
		log.trace("Device with " + serialStr + " attached");
	}

	protected abstract void onDetach(UsbDevice device);

	private void detach(UsbDevice device) {
		if (!devices.contains(device)) {
			return;
		}

		device.removeUsbDeviceListener(this);
		this.onDetach(device);
		devices.remove(device);

		String serialStr = getSerialStr(device);
		log.trace("Device with " + serialStr + " detached");
	}

	private void markTroubled(UsbDevice device, Throwable cause) {
		if (troubled.contains(device)) {
			return;
		}

		troubled.add(device);
		this.detach(device);

		String serialStr = getSerialStr(device);
		log.error("Permanently detached device " + serialStr
				+ " due to unhandled issue", cause);
	}

	@Override
	public final void usbDeviceDetached(UsbDeviceEvent event) {
		this.detach(event.getUsbDevice());
	}

	@Override
	public final void errorEventOccurred(UsbDeviceErrorEvent event) {
		this.markTroubled(event.getUsbDevice(), null);
	}

	@Override
	public final void dataEventOccurred(UsbDeviceDataEvent event) {
		/* no data to handle */
	}

	private void searchDevices() throws UsbException {
		for (UsbDevice device : findDevices()) {
			if (devices.contains(device)) {
				continue;
			}

			/*
			 * If a device is marked as "troubled", that means it was once
			 * registered once but got disconnected due to an error. To prevent
			 * a continuous loop of connecting devices, encountering an error,
			 * and then disconnecting them again, troubled devices are ignored
			 * once they are marked.
			 */
			if (troubled.contains(device)) {
				continue;
			}

			if (this.isSeeking(device)) {
				this.attach(device);
			}
		}
	}

	/**
	 * @throws InputException
	 *             if no targeted USB devices were specified.
	 * @see #seekDevice(int, int)
	 */
	@Override
	protected void seek() throws UsbException {
		if (descs.isEmpty()) {
			throw new InputException("no USB devices specified");
		}

		long currentTime = System.currentTimeMillis();
		if (currentTime - lastSearch >= SEARCH_RATE) {
			this.searchDevices();
			this.lastSearch = currentTime;
		}
	}

	/**
	 * This method is called for each USB device that is currently registered to
	 * this seeker. If an exception is thrown, the seeker will mark the USB
	 * device to be "troubled", and automatically disconnect it. Afterwards, it
	 * will not be reconnected.
	 * 
	 * @param device
	 *            the USB device being polled.
	 * @throws Exception
	 *             if an error occurs.
	 */
	protected abstract void poll(UsbDevice device) throws Exception;

	@Override
	public void poll() {
		super.poll();

		Iterator<UsbDevice> devicesI = devices.iterator();
		while (devicesI.hasNext()) {
			UsbDevice device = devicesI.next();
			try {
				this.poll(device);
			} catch (Exception e) {
				devicesI.remove();
				this.markTroubled(device, e);
			}
		}
	}

}
