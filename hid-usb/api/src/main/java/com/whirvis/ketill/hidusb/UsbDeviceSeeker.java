package com.whirvis.ketill.hidusb;

import com.whirvis.ketill.IoDeviceSeeker;
import com.whirvis.ketill.IoDevice;
import com.whirvis.ketill.KetillException;
import org.usb4java.*;

import java.util.*;

public abstract class UsbDeviceSeeker<I extends IoDevice>
		extends IoDeviceSeeker<I> {

	private static final long SEARCH_RATE = 1000L;
	private static boolean initializedLibUsb = false;

	private static void initializeLibUsb() {
		if (initializedLibUsb) {
			return;
		}

		int result = LibUsb.init(null);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException(result);
		}

		initializedLibUsb = true;
	}

	private static Set<Device> findDevices() {
		DeviceList list = new DeviceList();
		int result = LibUsb.getDeviceList(null, list);
		if (result < 0) {
			throw new LibUsbException(result);
		}

		/*
		 * Just to be safe, take every device from the device list and throw it
		 * into this set. The list is a handle for some native memory, which
		 * must in turn be freed as well. I personally do not trust myself to
		 * remember to free this list outside of this method.
		 */
		Set<Device> devices = new HashSet<>();
		for (Device device : list) {
			devices.add(device);
		}

		/*
		 * Now that the devices have been transferred to garbage collected
		 * memory, free the list handle (but keep the devices in tact.) The
		 * second parameter *must* be false. If it is true, the devices will all
		 * be freed from memory, making them dead on arrival.
		 */
		LibUsb.freeDeviceList(list, false);
		return devices;
	}

	private static String getSerialStr(DeviceHandle handle) {
		Device device = LibUsb.getDevice(handle);
		DeviceDescriptor desc = new DeviceDescriptor();
		int result = LibUsb.getDeviceDescriptor(device, desc);
		if (result != LibUsb.SUCCESS) {
			return "unretrievable serial number";
		}

		byte index = desc.iSerialNumber();
		String serial = LibUsb.getStringDescriptor(handle, index);
		if (serial != null) {
			return "serial number " + serial;
		}
		return "unknown serial number";
	}

	private final Set<DeviceDesc> descs;
	private final Map<Device, DeviceHandle> handles;
	private final Set<Device> troubled;
	private long lastSearch;

	/**
	 * After initial setup, this constructor will initialize LibUsb if it has
	 * not been initialized already.
	 *
	 * @see #seekDevice(int, int)
	 */
	public UsbDeviceSeeker() {

		this.descs = new HashSet<>();
		this.handles = new HashMap<>();
		this.troubled = new HashSet<>();

		/* ensure LibUsb is initialized */
		initializeLibUsb();
	}

	public boolean isSeeking(int vendorId, int productId) {
		for (DeviceDesc desc : descs) {
			if (desc.vendorId == vendorId && desc.productId == productId) {
				return true;
			}
		}
		return false;
	}

	private boolean isSeeking(Device device) {
		DeviceDescriptor desc = new DeviceDescriptor();
		int result = LibUsb.getDeviceDescriptor(device, desc);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException(result);
		}
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
		// log.debug("Seeking devices with ID " + idStr);
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
		Iterator<Device> devicesI = handles.keySet().iterator();
		while (devicesI.hasNext()) {
			Device device = devicesI.next();
			DeviceHandle handle = handles.get(device);
			if (this.isSeeking(device)) {
				devicesI.remove();
				this.detach(handle);
				count++;
			}
		}

		String idStr = DeviceDesc.getStr(vendorId, productId);
		// log.debug("Dropped " + count + " devices with ID " + idStr);
	}

	protected abstract void onAttach(DeviceHandle handle);

	private void attach(Device device) {
		if (handles.containsKey(device)) {
			return;
		}

		DeviceHandle handle = new DeviceHandle();
		int result = LibUsb.open(device, handle);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException(result);
		}

		/*
		 * Only increment the device's reference count after it has been opened
		 * successfully. This should prevent a memory leak if opening the device
		 * fails. The reference count is increased here as it will be decreased
		 * later when the device is detached.
		 */
		LibUsb.refDevice(device);
		handles.put(device, handle);

		this.onAttach(handle);
		String serialStr = getSerialStr(handle);
		// log.trace("Device with " + serialStr + " attached");
	}

	protected abstract void onDetach(DeviceHandle handle);

	private void detach(DeviceHandle handle) {
		Device device = LibUsb.getDevice(handle);

		this.onDetach(handle);
		String serialStr = getSerialStr(handle);
		// log.trace("Device with " + serialStr + " detached");

		/*
		 * Now that the device is no longer being used by this seeker, its
		 * reference count must be decreased. If this is not done, a leak in
		 * memory is sure to follow.
		 */
		handles.remove(device);
		LibUsb.unrefDevice(device);
	}
	
	protected abstract void onTrouble(DeviceHandle handle, Throwable cause);

	private void markTroubled(DeviceHandle handle, Throwable cause) {
		Device device = LibUsb.getDevice(handle);
		if (troubled.contains(device)) {
			throw new IllegalStateException("already troubled");
		}

		troubled.add(device);
		this.onTrouble(handle, cause);

		/*
		 * The device handle will become invalid after the device is detached.
		 * As such, this information (namely, the serial string for the log
		 * output) must be fetched before the device is detached.
		 */
		String serialStr = getSerialStr(handle);
		this.detach(handle);
		//log.error("Detached device with serial number " + serialStr
		//		+ " permanently due to unhandled issue", cause);
	}

	private void searchDevices() {
		for (Device device : findDevices()) {
			if (handles.containsKey(device)) {
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
				LibUsb.unrefDevice(device);
				continue;
			}

			if (this.isSeeking(device)) {
				this.attach(device);
			} else {
				/*
				 * Only unref the device after it has been determined it will no
				 * longer be needed. If it gets attached, the attach method will
				 * increase its reference count to keep it in memory. Otherwise,
				 * it will be freed here since it is not being sought after.
				 */
				LibUsb.unrefDevice(device);
			}
		}
	}

	/**
	 * @throws KetillException
	 *             if no targeted USB devices were specified.
	 * @see #seekDevice(int, int)
	 */
	@Override
	protected void seekImpl() {
		if (descs.isEmpty()) {
			throw new KetillException("no USB devices specified");
		}

		long currentTime = System.currentTimeMillis();
		if (currentTime - lastSearch >= SEARCH_RATE) {
			this.searchDevices();
			this.lastSearch = currentTime;
		}

		Iterator<Device> devicesI = handles.keySet().iterator();
		while (devicesI.hasNext()) {
			Device device = devicesI.next();
			DeviceHandle handle = handles.get(device);
			try {
				this.poll(handle);
			} catch (Exception e) {
				devicesI.remove();
				this.markTroubled(handle, e);
			}
		}
	}

	/**
	 * This method is called for each USB device that is currently registered to
	 * this seeker. If an exception is thrown, the seeker will mark the USB
	 * device to be "troubled", and automatically disconnect it. Afterwards, it
	 * will not be reconnected.
	 *
	 * @throws Exception
	 *             if an error occurs.
	 */
	protected abstract void poll(DeviceHandle handle) throws Exception;

}
