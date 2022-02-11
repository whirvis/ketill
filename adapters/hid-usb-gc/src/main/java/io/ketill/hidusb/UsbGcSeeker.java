package com.whirvis.ketill.gc;

import com.whirvis.ketill.hidusb.UsbDeviceSeeker;
import io.ketill.gc.GcController;
import org.usb4java.DeviceHandle;

import java.util.HashMap;
import java.util.Map;

public class UsbGcSeeker extends UsbDeviceSeeker<GcController> {

    private final boolean allowMultiple;
    private final Map<DeviceHandle, GcUsbDevice> hubs;
    private final Map<GcUsbAdapter, GcController> controllers;

    /**
     * @param allowMultiple {@code true} if multiple USB GameCube adapters
     *                      should be recognized, {@code false} if only the
     *                      first one found should be used.
     */
    public UsbGcSeeker(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
        this.hubs = new HashMap<>();
        this.controllers = new HashMap<>();

        this.seekDevice(GcUsbDevice.VENDOR_ID, GcUsbDevice.PRODUCT_ID);
    }

    /**
     * Constructs a new {@code UsbGcSeeker} with support for multiple USB
     * GameCube adapters enabled.
     */
    public UsbGcSeeker() {
        this(true);
    }

    @Override
    protected void onAttach(DeviceHandle handle) {
        if (hubs.isEmpty() || allowMultiple) {
            hubs.put(handle, new GcUsbDevice(handle));
        }
    }

    @Override
    protected void onDetach(DeviceHandle device) {
        GcUsbDevice hub = hubs.remove(device);
        if (hub == null) {
            return;
        }

        hub.shutdown();
        for (GcUsbAdapter adapter : hub.getAdapters()) {
            GcController controller = controllers.get(adapter);
            if (controller != null) {
                this.forgetDevice(controller);
            }
        }
    }

    @Override
    protected void onTrouble(DeviceHandle handle, Throwable cause) {
        /* TODO: handle this situation */
    }

    @Override
    protected void poll(DeviceHandle device) {
        GcUsbDevice hub = hubs.get(device);
        if (hub == null) {
            return;
        }

        hub.poll();
        for (GcUsbAdapter adapter : hub.getAdapters()) {
            boolean connected = adapter.isPortConnected();
            boolean registered = controllers.containsKey(adapter);
            if (connected && !registered) {
                GcController controller = new GcController(adapter);
                controllers.put(adapter, controller);
                this.discoverDevice(controller);
            } else if (!connected && registered) {
                GcController controller = controllers.remove(adapter);
                this.forgetDevice(controller);
            }
        }
    }

}
