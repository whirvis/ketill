package io.ketill.hidusb;

import io.ketill.IoDevice;
import io.ketill.IoDeviceSeeker;
import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.HidServicesSpecification;
import org.hid4java.event.HidServicesEvent;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * An I/O device seeker for HID devices, using Hid4Java.
 * <p>
 * Most USB devices follow the HID protocol, which make them compatible
 * with this device seeker. However, there exist USB devices which do not.
 * {@link LibUsbDeviceSeeker} should be used for those.
 * <p>
 * <b>Note:</b> Before calling {@link #seek()}, the device seeker must be
 * told which devices to seek out via {@link #seek(int, int)}. If this is
 * neglected, an {@code IllegalStateException} will be thrown.
 *
 * @param <I> the I/O device type.
 */
public abstract class HidDeviceSeeker<I extends IoDevice>
        extends IoDeviceSeeker<I> implements HidServicesListener {

    public static final int DEFAULT_SCAN_INTERVAL = 1000;

    private final HidServices services;
    private final List<DeviceInfo> seeking;
    private final List<HidDevice> blacklisted;
    private final List<HidDevice> devices;
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
     * @param scanIntervalMs the interval in milliseconds between HID
     *                       device enumeration scans.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less
     *                                  than or equal to zero.
     */
    public HidDeviceSeeker(int scanIntervalMs) {
        if (scanIntervalMs <= 0) {
            throw new IllegalArgumentException("scanIntervalMs <= 0");
        }

        HidServicesSpecification specs = new HidServicesSpecification();
        specs.setAutoStart(false);
        specs.setScanInterval(scanIntervalMs);
        specs.setPauseInterval(0);

        this.services = HidManager.getHidServices(specs);
        services.addHidServicesListener(this);

        this.seeking = new ArrayList<>();
        this.devices = new ArrayList<>();
        this.blacklisted = new ArrayList<>();
    }

    /**
     * Constructs a new {@code HidDeviceSeeker} with the argument for
     * {@code scanIntervalMs} being {@value #DEFAULT_SCAN_INTERVAL}.
     */
    public HidDeviceSeeker() {
        this(DEFAULT_SCAN_INTERVAL);
    }

    /**
     * @param vendorId  the vendor ID.
     * @param productId the product ID.
     * @return {@code true} if this device seeker is seeking out HID devices
     * with {@code vendorId} and {@code productId}, {@code false} otherwise.
     */
    public boolean isSeeking(int vendorId, int productId) {
        for (DeviceInfo info : seeking) {
            if (info.vendorId == vendorId && info.productId == productId) {
                return true;
            }
        }
        return false;
    }

    private boolean isSeeking(@NotNull HidDevice device) {
        return this.isSeeking(device.getVendorId(), device.getProductId());
    }

    private boolean isProduct(@NotNull HidDevice device, int vendorId,
                              int productId) {
        return device.getVendorId() == vendorId
                && device.getProductId() == productId;
    }

    /**
     * Indicates to the seeker it should seek out HID devices with the
     * specified vendor and product ID. When such a device is located,
     * the {@link #onConnect(HidDevice)} callback will be executed.
     *
     * @param vendorId  the vendor ID.
     * @param productId the product ID.
     * @throws IllegalStateException if this HID device seeker has been
     *                               closed via {@link #close()}.
     */
    protected void seek(int vendorId, int productId) {
        this.requireOpen();
        if (!this.isSeeking(vendorId, productId)) {
            seeking.add(new DeviceInfo(vendorId, productId));
        }
    }

    /**
     * Indicates to the seeker it should no longer seek out HID devices with
     * the specified vendor and product ID. All currently connected devices
     * with the specified vendor and product ID will be disconnected.
     *
     * @param vendorId  the vendor ID.
     * @param productId the product ID.
     * @throws IllegalStateException if this HID device seeker has been
     *                               closed via {@link #close()}.
     * @see #onDisconnect(HidDevice)
     */
    protected void drop(int vendorId, int productId) {
        this.requireOpen();
        if (!this.isSeeking(vendorId, productId)) {
            return;
        }

        seeking.removeIf(info -> info.vendorId == vendorId
                && info.productId == productId);

        Iterator<HidDevice> devicesI = devices.iterator();
        while (devicesI.hasNext()) {
            HidDevice device = devicesI.next();
            if (this.isProduct(device, vendorId, productId)) {
                devicesI.remove();
                this.disconnect(device);
            }
        }
    }

    /**
     * Blacklists a device from this device seeker. When blacklisted, a device
     * will be forcefully disconnected. Afterwards, it will not be reconnected.
     *
     * @param device the HID device to blacklist.
     * @throws NullPointerException  if {@code device} is {@code null}.
     * @throws IllegalStateException if this HID device seeker has been
     *                               closed via {@link #close()}.
     */
    protected void blacklist(@NotNull HidDevice device) {
        Objects.requireNonNull(device, "device");
        this.requireOpen();
        if (!blacklisted.contains(device)) {
            blacklisted.add(device);
            this.disconnect(device);
        }
    }

    private void connect(@NotNull HidDevice device) {
        device.setNonBlocking(true);
        devices.add(device);
        this.onConnect(device);
    }

    private void disconnect(@NotNull HidDevice device) {
        devices.remove(device);
        this.onDisconnect(device);
        device.close();
    }

    @Override
    public final synchronized void hidDeviceAttached(HidServicesEvent event) {
        if (this.isClosed()) {
            return;
        }
        try {
            HidDevice device = event.getHidDevice();
            if (devices.contains(device) || blacklisted.contains(device)) {
                return; /* already connected or blacklisted */
            }

            if (this.isSeeking(device) && device.open()) {
                this.connect(device);
            }
        } catch (Exception e) {
            this.hidException = e;
        }
    }

    @Override
    public final synchronized void hidDeviceDetached(HidServicesEvent event) {
        if (this.isClosed()) {
            return;
        }
        try {
            HidDevice device = event.getHidDevice();
            if (!devices.contains(device)) {
                return; /* device not previously connected */
            }
            this.disconnect(event.getHidDevice());
        } catch (Exception e) {
            this.hidException = e;
        }
    }

    @Override
    public final synchronized void hidFailure(HidServicesEvent event) {
        if (this.isClosed()) {
            return;
        }
        try {
            this.blacklist(event.getHidDevice());
        } catch (Exception e) {
            this.hidException = e;
        }
    }

    /**
     * Called when an HID device has been connected. By default, I/O
     * operations for the device are set to be non-blocking. This can
     * be changed via {@link HidDevice#setNonBlocking(boolean)}.
     * <p>
     * <b>Note:</b> Connected devices are <i>not</i> discovered.
     * They must be discovered using {@link #discoverDevice(IoDevice)}.
     *
     * @param device the HID device.
     */
    protected abstract void onConnect(@NotNull HidDevice device);

    /**
     * Called when an HID device has been disconnected.
     * <p>
     * <b>Note:</b> Disconnected devices are <i>not</i> forgotten.
     * They must be forgotten using {@link #forgetDevice(IoDevice)}.
     *
     * @param device the HID device.
     */
    protected abstract void onDisconnect(@NotNull HidDevice device);

    @Override
    @MustBeInvokedByOverriders
    protected void seekImpl() throws Exception {
        if (seeking.isEmpty()) {
            throw new IllegalStateException("no HID devices targeted");
        } else if (hidException != null) {
            throw hidException;
        }

        if (!startedServices) {
            services.start();
            this.startedServices = true;
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void close() {
        super.close();

        seeking.clear();
        blacklisted.clear();

        Iterator<HidDevice> devicesI = devices.iterator();
        while (devicesI.hasNext()) {
            HidDevice device = devicesI.next();
            device.close();
            devicesI.remove();
        }

        services.stop();
    }

}
