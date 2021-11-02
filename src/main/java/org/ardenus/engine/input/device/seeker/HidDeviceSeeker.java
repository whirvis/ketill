package org.ardenus.engine.input.device.seeker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.InputDevice;
import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.HidServicesSpecification;
import org.hid4java.event.HidServicesEvent;

public abstract class HidDeviceSeeker extends DeviceSeeker
		implements HidServicesListener {

	private final Set<DeviceDesc> descs;
	private final Set<HidDevice> devices;
	private final Set<HidDevice> troubled;
	private final HidServices services;
	private boolean startedServices;

	/**
	 * @param type
	 *            the input device type.
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}.
	 * @see #seekDevice(int, int)
	 */
	public HidDeviceSeeker(Class<? extends InputDevice> type) {
		super(type);

		this.descs = new HashSet<>();
		this.devices = new HashSet<>();
		this.troubled = new HashSet<>();

		HidServicesSpecification specs = new HidServicesSpecification();
		specs.setAutoStart(false);
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
		if (device == null) {
			return false;
		}
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
		if (!this.isSeeking(vendorId, productId)) {
			descs.add(new DeviceDesc(vendorId, productId));
		}
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

		Iterator<HidDevice> devicesI = devices.iterator();
		while (devicesI.hasNext()) {
			HidDevice device = devicesI.next();
			if (this.isSeeking(device)) {
				devicesI.remove();
				this.disconnect(device);
			}
		}
	}

	protected abstract void onConnect(HidDevice device);

	private void connect(HidDevice device) {
		if (!devices.contains(device)) {
			device.open();
			device.setNonBlocking(true);
			this.onConnect(device);
			devices.add(device);
		}
	}

	protected abstract void onDisconnect(HidDevice device);

	private void disconnect(HidDevice device) {
		if (devices.contains(device)) {
			device.close();
			this.onDisconnect(device);
			devices.remove(device);
		}
	}

	@Override
	public final void hidDeviceAttached(HidServicesEvent event) {
		HidDevice device = event.getHidDevice();
		if (devices.contains(device)) {
			return;
		}

		/*
		 * If a device is marked as "troubled", that means it was once
		 * registered once but got disconnected due to an error. To prevent a
		 * continuous loop of connecting devices, encountering an error, and
		 * then disconnecting them again, troubled devices are ignored once they
		 * are marked.
		 */
		if (troubled.contains(device)) {
			return;
		}

		if (this.isSeeking(device)) {
			this.connect(device);
		}
	}

	@Override
	public final void hidDeviceDetached(HidServicesEvent event) {
		this.disconnect(event.getHidDevice());
	}

	@Override
	public final void hidFailure(HidServicesEvent event) {
		HidDevice device = event.getHidDevice();
		troubled.add(device);
		this.disconnect(device);
	}

	@Override
	protected void seek() {
		if (!startedServices) {
			services.start();
			this.startedServices = true;
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

	/**
	 * @throws InputException
	 *             if no targeted HID devices were specified.
	 * @see #seekDevice(int, int)
	 */
	@Override
	public void poll() {
		super.poll();
		if (descs.isEmpty()) {
			throw new InputException("no HID devices specified");
		}

		Iterator<HidDevice> devicesI = devices.iterator();
		while (devicesI.hasNext()) {
			HidDevice device = devicesI.next();
			try {
				this.poll(device);
			} catch (Exception e) {
				troubled.add(device);
				devicesI.remove();
				this.disconnect(device);
			}
		}
	}

}
