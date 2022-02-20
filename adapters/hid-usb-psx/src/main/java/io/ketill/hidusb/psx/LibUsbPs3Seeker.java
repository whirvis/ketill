package io.ketill.hidusb.psx;

import io.ketill.hidusb.LibUsbDeviceSeeker;
import io.ketill.psx.Ps3Controller;
import org.jetbrains.annotations.NotNull;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsbException;

import java.util.HashMap;
import java.util.Map;

public final class LibUsbPs3Seeker extends LibUsbDeviceSeeker<Ps3Controller> {

    private static final int VENDOR_SONY = 0x54C;
    private static final int PRODUCT_DS3 = 0x268;

    private final Map<DeviceHandle, Ps3Controller> sessions;

    /**
     * @param scanIntervalMs the interval in milliseconds between USB
     *                       device enumeration scans.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less
     *                                  than or equal to zero.
     * @throws LibUsbException          if LibUSB could not be initialized.
     */
    public LibUsbPs3Seeker(int scanIntervalMs) {
        super(scanIntervalMs);
        this.sessions = new HashMap<>();
        this.seekProduct(VENDOR_SONY, PRODUCT_DS3);
    }

    /**
     * Constructs a new {@code LibUsbPs3Seeker} with the argument for
     * {@code scanIntervalMs} being {@value #DEFAULT_SCAN_INTERVAL}.
     *
     * @throws LibUsbException if LibUSB could not be initialized.
     */
    public LibUsbPs3Seeker() {
        this(DEFAULT_SCAN_INTERVAL);
    }

    @Override
    protected void onDeviceAttach(@NotNull DeviceHandle handle) {
        Ps3Controller controller = new Ps3Controller((c, r) ->
                new LibUsbPs3Adapter(c, r, usbContext, handle));
        sessions.put(handle, controller);
        this.discoverDevice(controller);
    }

    @Override
    protected void onDeviceDetach(@NotNull DeviceHandle handle) {
        Ps3Controller controller = sessions.remove(handle);
        if (controller != null) {
            this.forgetDevice(controller);
        }
    }

}
