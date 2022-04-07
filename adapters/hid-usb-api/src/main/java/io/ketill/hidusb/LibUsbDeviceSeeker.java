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
    private final Map<L, LibUsbQueued<L>> connectQueue;

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

        this.connectQueue = new HashMap<>();
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
         * them in an int so the expected value is returned.
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
    protected void peripheralAttached(@NotNull L peripheral) {
        synchronized (connectQueue) {
            connectQueue.put(peripheral, new LibUsbQueued<>(peripheral, 3));
        }
    }

    @Override
    protected void peripheralDetached(@NotNull L peripheral) {
        synchronized (connectQueue) {
            connectQueue.remove(peripheral);
            this.disconnectPeripheral(peripheral);
        }
    }

    @Override
    protected final @NotNull Collection<@NotNull L> scanPeripherals() {
        return LibUsbDevice.getConnected(usbContext, deviceSupplier);
    }

    private boolean attemptOpen(LibUsbQueued<L> queued) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - queued.lastAttempt < 1000L) {
            return false;
        }

        try {
            queued.device.openHandle();
            this.connectPeripheral(queued.device);
            return true; /* device connected */
        } catch (LibUsbException e) {
            queued.attemptsLeft--;
            queued.lastAttempt = currentTime;
            if (queued.attemptsLeft <= 0) {
                this.blockPeripheral(queued.device, e, true);
                return true; /* all attempts used */
            }
            return false; /* failed to open */
        }
    }

    @Override
    @MustBeInvokedByOverriders
    protected void seekImpl() throws Exception {
        super.seekImpl();
        synchronized (connectQueue) {
            connectQueue.values().removeIf(this::attemptOpen);
        }
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
