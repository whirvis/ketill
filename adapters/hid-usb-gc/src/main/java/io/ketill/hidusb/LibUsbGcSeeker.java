package io.ketill.hidusb;

import io.ketill.AdapterSupplier;
import io.ketill.gc.GcController;
import org.jetbrains.annotations.NotNull;
import org.usb4java.DeviceHandle;

import java.util.HashMap;
import java.util.Map;

public final class LibUsbGcSeeker extends LibUsbDeviceSeeker<GcController> {

    private static final short VENDOR_NINTENDO = 0x057E;
    private static final short PRODUCT_GC_WIIU_ADAPTER = 0x0337;

    private final boolean allowMultiple;
    private final Map<DeviceHandle, GcWiiUAdapter> wiiUAdapters;
    private final Map<AdapterSupplier<?>, GcController> sessions;

    /**
     * @param allowMultiple {@code true} if multiple USB GameCube adapters
     *                      should be recognized, {@code false} if only the
     *                      first one discovered should be used.
     */
    public LibUsbGcSeeker(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
        this.wiiUAdapters = new HashMap<>();
        this.sessions = new HashMap<>();
        this.seek(VENDOR_NINTENDO, PRODUCT_GC_WIIU_ADAPTER);
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
        if (wiiUAdapters.isEmpty() || allowMultiple) {
            GcWiiUAdapter adapter = new GcWiiUAdapter(usbContext, handle);
            wiiUAdapters.put(handle, adapter);
        }
    }

    @Override
    protected void onDetach(@NotNull DeviceHandle device) {
        GcWiiUAdapter wiiUAdapter = wiiUAdapters.remove(device);
        if (wiiUAdapter == null) {
            return;
        }

        wiiUAdapter.close();
        for (int i = 0; i < GcWiiUAdapter.SLOT_COUNT; i++) {
            AdapterSupplier<?> supplier = wiiUAdapter.getSlotSupplier(i);
            GcController controller = sessions.remove(supplier);
            if (controller != null) {
                this.forgetDevice(controller);
            }
        }
    }

    private void seekAdapter(GcWiiUAdapter wiiUAdapter) {
        wiiUAdapter.poll();
        for (int i = 0; i < GcWiiUAdapter.SLOT_COUNT; i++) {
            AdapterSupplier<GcController> supplier =
                    wiiUAdapter.getSlotSupplier(i);

            boolean connected = wiiUAdapter.isSlotConnected(i);
            boolean registered = sessions.containsKey(supplier);

            if (connected && !registered) {
                GcController controller = new GcController(supplier);
                sessions.put(supplier, controller);
                this.discoverDevice(controller);
            } else if (!connected && registered) {
                GcController controller = sessions.remove(supplier);
                this.forgetDevice(controller);
            }
        }
    }

    @Override
    protected void seekImpl() {
        super.seekImpl();
        for (GcWiiUAdapter wiiUAdapter : wiiUAdapters.values()) {
            this.seekAdapter(wiiUAdapter);
        }
    }

}
