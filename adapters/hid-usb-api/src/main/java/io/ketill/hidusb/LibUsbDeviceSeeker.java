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

    protected static final int DEFAULT_SETUP_ATTEMPTS = 3;

    private final boolean userContext;
    private final Context usbContext;
    private final LibUsbDeviceSupplier<L> deviceSupplier;
    private final Map<L, LibUsbOpening<L>> openings;

    @SuppressWarnings("unchecked")
    private LibUsbDeviceSeeker(long scanIntervalMs, boolean userContext,
                               @NotNull Context context,
                               @NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        super(scanIntervalMs);
        this.userContext = userContext;
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
     * @param scanIntervalMs the interval in milliseconds between device
     *                       enumeration scans. This does <i>not</i> cause
     *                       {@link #seek()} to block. It only prevents a
     *                       device scan from being performed unless enough
     *                       time has elapsed between method calls.
     * @param context        the USB context. Since this was created by the
     *                       user, {@link #close()} will not exit it.
     * @param deviceSupplier the LibUSB device supplier. This can usually
     *                       just be {@code LibUsbDevice::new}.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less
     *                                  than {@value #MINIMUM_SCAN_INTERVAL}.
     * @throws NullPointerException     if {@code context} or
     *                                  {@code deviceSupplier}
     *                                  are {@code null}.
     * @see LibUsbDevice#initContext()
     */
    public LibUsbDeviceSeeker(long scanIntervalMs,
                              @NotNull Context context,
                              @NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        this(scanIntervalMs, true, context, deviceSupplier);
    }

    /**
     * Constructs a new {@code LibUsbDeviceSeeker} with the argument
     * for {@code context} being a newly initialized LibUSB context.
     * This context will be exited when the seeker is closed.
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
        this(scanIntervalMs, false, LibUsbDevice.initContext(), deviceSupplier);
    }

    /**
     * Constructs a new {@code LibUsbDeviceSeeker} with the argument for
     * {@code scanIntervalMs} being {@value #MINIMUM_SCAN_INTERVAL}.
     *
     * @param context        the USB context. Since this was created by the
     *                       user, {@link #close()} will not exit it.
     * @param deviceSupplier the LibUSB device supplier. This can usually
     *                       just be {@code LibUsbDevice::new}.
     * @throws NullPointerException if {@code context} or
     *                              {@code deviceSupplier} are {@code null}.
     * @see LibUsbDevice#initContext()
     */
    public LibUsbDeviceSeeker(@NotNull Context context,
                              @NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        this(MINIMUM_SCAN_INTERVAL, true, context, deviceSupplier);
    }

    /**
     * Constructs a new {@code LibUsbDeviceSeeker} with the argument for
     * {@code scanIntervalMs} being {@value #MINIMUM_SCAN_INTERVAL} and
     * {@code context} being a newly initialized LibUSB context. This
     * context will be exited when the seeker is closed.
     *
     * @param deviceSupplier the LibUSB device supplier. This can usually
     *                       just be {@code LibUsbDevice::new}.
     * @throws NullPointerException if {@code deviceSupplier} is {@code null}.
     * @throws LibUsbException      if LibUSB could not be initialized.
     */
    public LibUsbDeviceSeeker(@NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        this(MINIMUM_SCAN_INTERVAL, false, LibUsbDevice.initContext(),
                deviceSupplier);
    }

    /**
     * By default, this method returns {@value #DEFAULT_SETUP_ATTEMPTS}.
     *
     * @param peripheral the peripheral.
     * @return the amount of attempts {@code peripheral} should be afforded
     * for connection. Values less than one are redundant, all devices are
     * afforded one attempt.
     * @throws NullPointerException if {@code peripheral} is {@code null}.
     */
    protected int getSetupAttempts(@NotNull L peripheral) {
        Objects.requireNonNull(peripheral, "peripheral cannot be null");
        return DEFAULT_SETUP_ATTEMPTS;
    }

    @Override
    protected final @NotNull ProductId getId(@NotNull L peripheral) {
        return peripheral.getProductId();
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
            int setupAttempts = this.getSetupAttempts(peripheral);
            openings.put(peripheral, new LibUsbOpening<>(peripheral,
                    setupAttempts));
        }

        /*
         * Attempt to open the handle here. If no exception is thrown here,
         * the peripheral can be removed the opening tracker. If this fails,
         * the setup failure handler will take care of it.
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
         * Sometimes, opening a device handle will fail for no discernable
         * reason. When this happens, decrement the remaining attempt count.
         * If any attempts remain, setup will be attempted once more on the
         * next peripheral scan. If no attempts remain, block the peripheral
         * until it is detached.
         */
        LibUsbOpening<L> queued = openings.get(peripheral);
        queued.remainingAttempts--;
        if (queued.remainingAttempts <= 0) {
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

        /*
         * If the LibUSB context being used was not created by the user, the
         * seeker should exit it here; thus freeing allocated resources. The
         * seeker must not exit a user created context, as it is considered
         * to be the responsibility of the user. Furthermore, it allows them
         * to continue using it after this seeker has been closed.
         */
        if (!userContext) {
            LibUsbDevice.exitContext(usbContext);
        }
    }

}
