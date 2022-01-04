package com.whirvis.kibasan.seeker;

import java.util.HashMap;
import java.util.Map;

import org.usb4java.DeviceHandle;

import com.whirvis.kibasan.Ps3Controller;
import com.whirvis.kibasan.adapter.dualshock.Ds3UsbAdapter;

public class UsbDs3Seeker extends UsbDeviceSeeker {

	private static final int VENDOR_ID = 0x54C;
	private static final int PRODUCT_ID = 0x268;

	private final Map<DeviceHandle, Ps3Controller> controllers;

	public UsbDs3Seeker() {
		super(Ps3Controller.class);
		this.controllers = new HashMap<>();
		this.seekDevice(VENDOR_ID, PRODUCT_ID);
	}

	@Override
	protected void onAttach(DeviceHandle handle) {
		Ps3Controller controller =
				new Ps3Controller(new Ds3UsbAdapter(handle));
		controllers.put(handle, controller);
		this.register(controller);
	}

	@Override
	protected void onDetach(DeviceHandle handle) {
		Ps3Controller controller = controllers.remove(handle);
		if (controller != null) {
			this.unregister(controller);
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
