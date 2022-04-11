package io.ketill.hidusb.psx;

import io.ketill.hidusb.LibUsbDevice;
import io.ketill.hidusb.LibUsbDeviceSeeker;
import io.ketill.psx.Ps3Controller;
import org.jetbrains.annotations.NotNull;
import org.usb4java.Context;
import org.usb4java.LibUsbException;

import java.util.HashMap;
import java.util.Map;

public final class LibUsbPs3Seeker
        extends LibUsbDeviceSeeker<Ps3Controller, LibUsbDevicePs3> {

    private static final int VENDOR_SONY = 0x054C;
    private static final int PRODUCT_DS3 = 0x0268;

    private final Map<LibUsbDevicePs3, Ps3Controller> sessions;

    /**
     * @param scanIntervalMs the interval in milliseconds between device
     *                       enumeration scans. This does <i>not</i> cause
     *                       {@link #seek()} to block. It only prevents a
     *                       device scan from being performed unless enough
     *                       time has elapsed between method calls.
     * @param context        the USB context.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less
     *                                  than {@value #MINIMUM_SCAN_INTERVAL}.
     * @throws NullPointerException     if {@code context} is {@code null}.
     * @see LibUsbDevice#initContext()
     */
    public LibUsbPs3Seeker(long scanIntervalMs, @NotNull Context context) {
        super(scanIntervalMs, context, LibUsbDevicePs3::new);
        this.sessions = new HashMap<>();
        this.targetProduct(VENDOR_SONY, PRODUCT_DS3);
    }

    /**
     * Constructs a new {@code LibUsbPs3Seeker} with the argument for
     * {@code context} being a newly initialized LibUSB context.
     *
     * @param scanIntervalMs the interval in milliseconds between device
     *                       enumeration scans. This does <i>not</i> cause
     *                       {@link #seek()} to block. It only prevents a
     *                       device scan from being performed unless enough
     *                       time has elapsed between method calls.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less
     *                                  than {@value #MINIMUM_SCAN_INTERVAL}.
     * @throws LibUsbException          if LibUSB could not be initialized.
     */
    public LibUsbPs3Seeker(long scanIntervalMs) {
        this(scanIntervalMs, LibUsbDevice.initContext());
    }

    /**
     * Constructs a new {@code LibUsbPs3Seeker} with the argument for
     * {@code scanIntervalMs} being {@value #MINIMUM_SCAN_INTERVAL}.
     * <p>
     * <b>Note:</b> The scan interval does <i>not</i> cause {@link #seek()}
     * to block. It only prevents a device scan from being performed unless
     * enough time has elapsed between method calls.
     *
     * @param context the USB context.
     * @throws NullPointerException if {@code context} is {@code null}.
     * @see LibUsbDevice#initContext()
     */
    public LibUsbPs3Seeker(@NotNull Context context) {
        this(MINIMUM_SCAN_INTERVAL, context);
    }

    /**
     * Constructs a new {@code LibUsbPs3Seeker} with the argument for
     * {@code scanIntervalMs} being {@value #MINIMUM_SCAN_INTERVAL} and
     * {@code context} being a newly initialized LibUSB context.
     * <p>
     * <b>Note:</b> The scan interval does <i>not</i> cause {@link #seek()}
     * to block. It only prevents a device scan from being performed unless
     * enough time has elapsed between method calls.
     *
     * @throws LibUsbException if LibUSB could not be initialized.
     */
    public LibUsbPs3Seeker() {
        this(MINIMUM_SCAN_INTERVAL);
    }

    @Override
    protected void peripheralConnected(@NotNull LibUsbDevicePs3 usbDevice) {
        Ps3Controller controller = new Ps3Controller((c, r) ->
                new LibUsbPs3Adapter(c, r, usbDevice));
        sessions.put(usbDevice, controller);
        this.discoverDevice(controller);
    }

    @Override
    protected void peripheralDisconnected(@NotNull LibUsbDevicePs3 usbDevice) {
        Ps3Controller controller = sessions.remove(usbDevice);
        if (controller != null) {
            this.forgetDevice(controller);
        }
    }

}
