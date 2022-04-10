package io.ketill.hidusb;

import io.ketill.IoDevice;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.usb4java.Context;
import org.usb4java.LibUsbException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An I/O device seeker for USB devices, using LibUSB.
 * <p>
 * While this device seeker can be used for any device connected via USB,
 * users should be aware {@link HidDeviceSeeker} exists. When a USB device
 * conforms to the HID protocol, the HID device seeker should be used. The
 * LibUSB device seeker exists mainly for devices which do not conform to
 * the HID protocol.
 * <p>
 * <b>Note:</b> Before calling {@link #seek()}, the device seeker must be
 * told which devices to seek out via {@link #targetProduct(ProductId)}. If
 * this is neglected, an {@code IllegalStateException} will be thrown.
 *
 * @param <I> the I/O device type.
 * @param <L> the LibUSB device type. This can usually just be
 *            {@link LibUsbDevice}. However, if additional features of
 *            LibUSB must be implemented, extend that class and use it
 *            as the template type here instead.
 */
public abstract class LibUsbDeviceSeeker<I extends IoDevice,
        L extends LibUsbDevice> extends PeripheralSeeker<I, L> {

    private final Context usbContext;
    private final LibUsbDeviceSupplier<L> deviceSupplier;
    private final Map<L, LibUsbOpening<L>> openings;

    /**
     * @param scanIntervalMs the interval in milliseconds between device
     *                       enumeration scans. This does <i>not</i> cause
     *                       {@link #seek()} to block. It only prevents a
     *                       device scan from being performed unless enough
     *                       time has elapsed between method calls.
     * @param context        the USB context.
     * @param deviceSupplier the LibUSB device supplier. This can usually
     *                       just be {@code LibUsbDevice::new}.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less
     *                                  than {@value #MINIMUM_SCAN_INTERVAL}.
     * @throws NullPointerException     if {@code context} or
     *                                  {@code deviceSupplier}
     *                                  are {@code null}.
     * @see LibUsbDevice#initContext()
     */
    @SuppressWarnings("unchecked")
    public LibUsbDeviceSeeker(long scanIntervalMs,
                              @NotNull Context context,
                              @NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        super(scanIntervalMs);
        this.usbContext = Objects.requireNonNull(context,
                "default context is forbidden");

        /*
         * While this is an unchecked cast, the template requires that the
         * type extend LibUsbDevice. As such, this cast is safe to perform.
         */
        Objects.requireNonNull(deviceSupplier,
                "deviceSupplier cannot be null");
        this.deviceSupplier = (LibUsbDeviceSupplier<L>) deviceSupplier;

        this.openings = new HashMap<>();
    }

    /**
     * Constructs a new {@code LibUsbDeviceSeeker} with the argument for
     * {@code context} being a newly initialized LibUSB context.
     *
     * @param scanIntervalMs the interval in milliseconds between device
     *                       enumeration scans. This does <i>not</i> cause
     *                       {@link #seek()} to block. It only prevents a
     *                       device scan from being performed unless enough
     *                       time has elapsed between method calls.
     * @param deviceSupplier the LibUSB device supplier. This can usually
     *                       just be {@code LibUsbDevice::new}.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less
     *                                  than {@value #MINIMUM_SCAN_INTERVAL}.
     * @throws NullPointerException     if {@code deviceSupplier}
     *                                  is {@code null}.
     * @throws LibUsbException          if LibUSB could not be initialized.
     */
    public LibUsbDeviceSeeker(long scanIntervalMs,
                              @NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        this(scanIntervalMs, LibUsbDevice.initContext(), deviceSupplier);
    }

    /**
     * Constructs a new {@code LibUsbDeviceSeeker} with the argument for
     * {@code scanIntervalMs} being {@value #MINIMUM_SCAN_INTERVAL}.
     *
     * @param context        the USB context.
     * @param deviceSupplier the LibUSB device supplier. This can usually
     *                       just be {@code LibUsbDevice::new}.
     * @throws NullPointerException if {@code context} or
     *                              {@code deviceSupplier} are {@code null}.
     * @see LibUsbDevice#initContext()
     */
    public LibUsbDeviceSeeker(@NotNull Context context,
                              @NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        this(MINIMUM_SCAN_INTERVAL, context, deviceSupplier);
    }

    /**
     * Constructs a new {@code LibUsbDeviceSeeker} with the argument for
     * {@code scanIntervalMs} being {@value #MINIMUM_SCAN_INTERVAL} and
     * {@code context} being a newly initialized LibUSB context.
     *
     * @param deviceSupplier the LibUSB device supplier. This can usually
     *                       just be {@code LibUsbDevice::new}.
     * @throws NullPointerException if {@code deviceSupplier} is {@code null}.
     * @throws LibUsbException      if LibUSB could not be initialized.
     */
    public LibUsbDeviceSeeker(@NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        this(MINIMUM_SCAN_INTERVAL, LibUsbDevice.initContext(), deviceSupplier);
    }

    @Override
    protected final @NotNull ProductId getId(@NotNull L peripheral) {
        /*
         * Vendor IDs and product IDs are unsigned shorts. However,
         * the underlying LibUSB API returns them as a signed Java
         * short. This converts them to an unsigned value and stores
         * them as an int so the expected value is returned.
         */
        int vendorId = peripheral.usbDescriptor.idVendor() & 0xFFFF;
        int productId = peripheral.usbDescriptor.idProduct() & 0xFFFF;
        return new ProductId(vendorId, productId);
    }

    @Override
    protected final int getHash(@NotNull L peripheral) {
        return peripheral.hashCode();
    }

    @Override
    protected final @NotNull Collection<@NotNull L> scanPeripherals() {
        return LibUsbDevice.getConnected(usbContext, deviceSupplier);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void setupPeripheral(@NotNull L peripheral) {
        if (!openings.containsKey(peripheral)) {
            openings.put(peripheral, new LibUsbOpening<>(peripheral, 3));
        }

        /*
         * Attempt to open the handle here. If no exception is thrown
         * here, the peripheral will be removed the opening tracker.
         * Otherwise, the setup failure handle will take over.
         */
        peripheral.openHandle();
        openings.remove(peripheral);
    }

    @Override
    protected void peripheralSetupFailed(@NotNull L peripheral,
                                         @NotNull Throwable cause) {
        if (!(cause instanceof LibUsbException)) {
            super.peripheralSetupFailed(peripheral, cause);
            return; /* something else went wrong here */
        }

        /*
         * Sometimes, opening a device handle fails (for no discernable
         * reason). When this happens, decrement the amount of remaining
         * attempts. If any attempts remain, setup will be attempted once
         * more on the next peripheral scan. If no attempts remain, block
         * the peripheral until it is detached.
         */
        LibUsbOpening<L> queued = openings.get(peripheral);
        queued.attemptsLeft--;
        if (queued.attemptsLeft <= 0) {
            openings.remove(peripheral);
            this.blockPeripheral(queued.device, cause, true);
        }
    }

    @Override
    @MustBeInvokedByOverriders
    protected void shutdownPeripheral(@NotNull L peripheral) {
        openings.remove(peripheral);
        peripheral.close();
    }

    @Override
    @MustBeInvokedByOverriders
    public void close() {
        if (this.isClosed()) {
            return;
        }
        super.close();
        LibUsbDevice.exitContext(usbContext);
    }

}
