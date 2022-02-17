package io.ketill.hidusb;

import io.ketill.gc.GcController;
import org.jetbrains.annotations.NotNull;
import org.usb4java.DeviceHandle;

import java.util.HashMap;
import java.util.Map;

public class LibUsbGcSeeker extends LibUsbDeviceSeeker<GcController> {

    private final boolean allowMultiple;
    private final Map<DeviceHandle, GcUsbHubDevice> hubs;
    private final Map<LibUsbGcAdapterSupplier, GcController> controllers;

    /**
     * @param allowMultiple {@code true} if multiple USB GameCube adapters
     *                      should be recognized, {@code false} if only the
     *                      first one found should be used.
     */
    public LibUsbGcSeeker(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
        this.hubs = new HashMap<>();
        this.controllers = new HashMap<>();

        this.seek(GcUsbHubDevice.VENDOR_ID, GcUsbHubDevice.PRODUCT_ID);
    }

    /**
     * Constructs a new {@code UsbGcSeeker} with support for multiple USB
     * GameCube adapters enabled.
     */
    public LibUsbGcSeeker() {
        this(true);
    }

    @Override
    protected void onAttach(@NotNull DeviceHandle handle) {
        if (hubs.isEmpty() || allowMultiple) {
            hubs.put(handle, new GcUsbHubDevice(handle));
        }
    }

    @Override
    protected void onDetach(@NotNull DeviceHandle device) {
        GcUsbHubDevice hub = hubs.remove(device);
        if (hub == null) {
            return;
        }

        hub.close();
        for (LibUsbGcAdapterSupplier adapter : hub.getAdapterSuppliers()) {
            GcController controller = controllers.get(adapter);
            if (controller != null) {
                this.forgetDevice(controller);
            }
        }
    }

    private void seekHub(GcUsbHubDevice hub) {
        hub.poll();


        for (LibUsbGcAdapterSupplier adapter : hub.getAdapterSuppliers()) {
            boolean connected = adapter.shouldGet();
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

    @Override
    protected void seekImpl() {
        super.seekImpl();
        for(GcUsbHubDevice hub : hubs.values()) {
            this.seekHub(hub);
        }
    }

}
