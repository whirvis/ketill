package com.whirvis.ketill.dualshock;

import com.whirvis.ketill.hidusb.UsbDeviceSeeker;
import com.whirvis.ketill.psx.Ps3Controller;
import org.usb4java.DeviceHandle;

import java.util.HashMap;
import java.util.Map;

public class UsbDs3Seeker extends UsbDeviceSeeker<Ps3Controller> {

	private static final int VENDOR_ID = 0x54C;
	private static final int PRODUCT_ID = 0x268;

	private final Map<DeviceHandle, Ps3Controller> controllers;

	public UsbDs3Seeker() {
		this.controllers = new HashMap<>();
		this.seekDevice(VENDOR_ID, PRODUCT_ID);
	}

	@Override
	protected void onAttach(DeviceHandle handle) {
		Ps3Controller controller =
				new Ps3Controller(new Ds3UsbAdapter(handle));
		controllers.put(handle, controller);
		this.discoverDevice(controller);
	}

	@Override
	protected void onDetach(DeviceHandle handle) {
		Ps3Controller controller = controllers.remove(handle);
		if (controller != null) {
			this.forgetDevice(controller);
		}
	}

	@Override
	protected void onTrouble(DeviceHandle handle, Throwable cause) {
		/* TODO: handle this situation */
	}

	@Override
	protected void poll(DeviceHandle device) {
		/* nothing to poll */
	}

}
