package io.ketill.hidusb.gc;

import io.ketill.AdapterSupplier;
import io.ketill.gc.GcController;
import io.ketill.hidusb.LibUsbDeviceSeeker;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class LibUsbGcSeeker
        extends LibUsbDeviceSeeker<GcController, LibUsbDeviceGc> {

    private static final short VENDOR_NINTENDO = 0x057E;
    private static final short PRODUCT_GC_WIIU_ADAPTER = 0x0337;

    private final boolean allowMultiple;
    private final Map<LibUsbDeviceGc, GcWiiUAdapter> wiiUAdapters;
    private final Map<AdapterSupplier<?>, GcController> sessions;

    /**
     * @param allowMultiple {@code true} if multiple Nintendo Wii U GameCube
     *                      adapters should be recognized, {@code false} if
     *                      only the first one connected should be used.
     */
    public LibUsbGcSeeker(boolean allowMultiple) {
        super(LibUsbDeviceGc::new);

        this.allowMultiple = allowMultiple;
        this.wiiUAdapters = new HashMap<>();
        this.sessions = new HashMap<>();

        this.targetProduct(VENDOR_NINTENDO, PRODUCT_GC_WIIU_ADAPTER);
    }

    /**
     * Constructs a new {@code LibUsbGcSeeker} with support for multiple
     * Nintendo Wii U GameCube adapters enabled.
     */
    public LibUsbGcSeeker() {
        this(true);
    }

    @Override
    protected void peripheralConnected(@NotNull LibUsbDeviceGc usbDevice) {
        if (wiiUAdapters.isEmpty() || allowMultiple) {
            GcWiiUAdapter adapter = new GcWiiUAdapter(usbDevice);
            wiiUAdapters.put(usbDevice, adapter);
        }
    }

    @Override
    protected void peripheralDisconnected(@NotNull LibUsbDeviceGc usbDevice) {
        GcWiiUAdapter wiiUAdapter = wiiUAdapters.remove(usbDevice);
        if (wiiUAdapter == null) {
            return; /* adapter not used */
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
    protected void seekImpl() throws Exception {
        super.seekImpl();
        for (GcWiiUAdapter wiiUAdapter : wiiUAdapters.values()) {
            this.seekAdapter(wiiUAdapter);
        }
    }

}
