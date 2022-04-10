package io.ketill.hidusb;

import io.ketill.IoDevice;
import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesSpecification;
import org.hid4java.event.HidServicesEvent;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An I/O device seeker for HID devices, using Hid4Java.
 * <p>
 * Most USB devices follow the HID protocol, which make them compatible
 * with this device seeker. However, there exist USB devices which do not.
 * {@link LibUsbDeviceSeeker} should be used for those.
 * <p>
 * <b>Note:</b> Before calling {@link #seek()}, the device seeker must
 * be told which devices to seek out via {@link #targetProduct(ProductId)}.
 * If this is neglected, an {@code IllegalStateException} will be thrown.
 *
 * @param <I> the I/O device type.
 */
public abstract class HidDeviceSeeker<I extends IoDevice>
        extends PeripheralSeeker<I, HidDevice> {

    private final List<HidDevice> scanned;
    private final HidServices services;
    private boolean startedServices;

    /*
     * Because the callback methods of HidServicesListener drop any
     * exceptions thrown into the void, all of their code is wrapped
     * into a try catch all block. If they experience any exceptions,
     * they will store them into this variable. Afterwards, seekImpl()
     * will throw it on their behalf.
     */
    private Exception hidException;

    /**
     * @param scanIntervalMs the interval in milliseconds between device
     *                       enumeration scans. This does <i>not</i> cause
     *                       {@link #seek()} to block. It only prevents a
     *                       device scan from being performed unless enough
     *                       time has elapsed between method calls.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less
     *                                  than {@value #MINIMUM_SCAN_INTERVAL};
     *                                  if {@code scanIntervalMs} is greater
     *                                  than {@value Integer#MAX_VALUE}.
     */
    public HidDeviceSeeker(long scanIntervalMs) {
        super(scanIntervalMs);

        /* Hid4Java uses an int for the scan interval */
        if (scanIntervalMs > Integer.MAX_VALUE) {
            String msg = "scanIntervalMs cannot be greater than";
            msg += " " + Integer.MAX_VALUE;
            throw new IllegalArgumentException(msg);
        }

        HidServicesSpecification specs = new HidServicesSpecification();
        specs.setAutoStart(false);
        specs.setScanInterval((int) scanIntervalMs);
        specs.setPauseInterval(0);

        this.scanned = new CopyOnWriteArrayList<>();
        this.services = HidManager.getHidServices(specs);
        services.addHidServicesListener(new HidDeviceListener(this));
    }

    /**
     * Constructs a new {@code HidDeviceSeeker} with the argument for
     * {@code scanIntervalMs} being {@value #MINIMUM_SCAN_INTERVAL}.
     * <p>
     * <b>Note:</b> The scan interval does <i>not</i> cause {@link #seek()}
     * to block. It only prevents a device scan from being performed unless
     * enough time has elapsed between method calls.
     */
    public HidDeviceSeeker() {
        this(MINIMUM_SCAN_INTERVAL);
    }

    @Override
    protected final @NotNull ProductId getId(@NotNull HidDevice peripheral) {
        int vendorId = peripheral.getVendorId();
        int productId = peripheral.getProductId();
        return new ProductId(vendorId, productId);
    }

    @Override
    protected final int getHash(@NotNull HidDevice peripheral) {
        return peripheral.hashCode();
    }

    /* package-private for listener */
    synchronized void hidDeviceAttached(HidServicesEvent event) {
        if (this.isClosed()) {
            return;
        }

        try {
            HidDevice device = event.getHidDevice();
            if (!scanned.contains(device)) {
                scanned.add(device);
            }
        } catch (Exception e) {
            this.hidException = e;
        }
    }

    /* package-private for listener */
    synchronized void hidDeviceDetached(HidServicesEvent event) {
        if (this.isClosed()) {
            return;
        }

        try {
            scanned.remove(event.getHidDevice());
        } catch (Exception e) {
            this.hidException = e;
        }
    }

    /* package-private for listener */
    synchronized void hidFailure(HidServicesEvent event) {
        if (this.isClosed()) {
            return;
        }

        try {
            HidDevice device = event.getHidDevice();
            if (!this.isPeripheralBlocked(device)) {
                this.blockPeripheral(device, true);
            }
        } catch (Exception e) {
            this.hidException = e;
        }
    }

    @Override
    protected final @NotNull Collection<@NotNull HidDevice> scanPeripherals() {
        return this.scanned;
    }

    @Override
    protected final void setupPeripheral(@NotNull HidDevice peripheral) {
        if (peripheral.open()) {
            peripheral.setNonBlocking(true);
        } else {
            this.blockPeripheral(peripheral, true);
        }
    }

    @Override
    protected final void shutdownPeripheral(@NotNull HidDevice peripheral) {
        peripheral.close();
    }

    @Override
    protected final void seekImpl() throws Exception {
        if (hidException != null) {
            throw hidException;
        }

        if (!startedServices) {
            services.start();
            this.startedServices = true;
        }

        super.seekImpl();
    }

    @Override
    @MustBeInvokedByOverriders
    public void close() {
        if (this.isClosed()) {
            return;
        }
        super.close();
        services.stop();
    }

}
