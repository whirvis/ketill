package io.ketill.hidusb;

import io.ketill.IoDevice;
import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesSpecification;
import org.hid4java.event.HidServicesEvent;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An I/O device seeker for HID devices, using Hid4Java.
 * <p>
 * Most USB devices follow the HID protocol, which make them compatible
 * with this device seeker. However, there exist USB devices which do not.
 * {@link LibUsbDeviceSeeker} should be used for those.
 * <p>
 * <b>Note:</b> Before calling {@link #seek()}, the device seeker must
 * be told which devices to seek out via {@link #targetProduct(DeviceId)}.
 * If this is neglected, an {@code IllegalStateException} will be thrown.
 *
 * @param <I> the I/O device type.
 */
public abstract class HidDeviceSeeker<I extends IoDevice>
        extends SystemDeviceSeeker<I, HidDevice> {

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
     *                                  than {@value #MINIMUM_SCAN_INTERVAL}
     *                                  or greater than
     *                                  {@value Integer#MAX_VALUE}.
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

        this.scanned = new ArrayList<>();
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

    /* package-private for testing */
    synchronized void hidDeviceAttached(HidServicesEvent event) {
        scanned.add(event.getHidDevice());
    }

    /* package-private for testing */
    synchronized void hidDeviceDetached(HidServicesEvent event) {
        scanned.add(event.getHidDevice());
    }

    /* package-private for testing */
    synchronized void hidFailure(HidServicesEvent event) {
        HidDevice device = event.getHidDevice();
        if (!this.isClosed() && !this.isBlocked(device)) {
            try {
                this.blockDevice(device, true);
            } catch (Exception e) {
                this.hidException = e;
            }
        }
    }

    @Override
    protected final @NotNull DeviceId getDeviceId(@NotNull HidDevice device) {
        int vendorId = device.getVendorId();
        int productId = device.getProductId();
        return new DeviceId(vendorId, productId);
    }

    @Override
    protected final int getDeviceHash(@NotNull HidDevice device) {
        return device.hashCode();
    }

    @Override
    protected final @NotNull Collection<@NotNull HidDevice> scanDevices() {
        return this.scanned;
    }

    @Override
    protected void onDeviceAttach(@NotNull HidDevice device) {
        try {
            if (device.open()) {
                device.setNonBlocking(true);
                this.connectDevice(device);
            } else {
                this.blockDevice(device, true);
            }
        } catch (Exception e) {
            this.hidException = e;
        }
    }

    @Override
    protected void onDeviceDetach(@NotNull HidDevice device) {
        try {
            device.close();
            this.disconnectDevice(device, true);
        } catch (Exception e) {
            this.hidException = e;
        }
    }

    @Override
    @MustBeInvokedByOverriders
    protected void seekImpl() throws Exception {
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
