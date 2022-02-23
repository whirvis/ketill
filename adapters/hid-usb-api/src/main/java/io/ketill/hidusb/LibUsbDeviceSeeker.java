package io.ketill.hidusb;

import io.ketill.IoDevice;
import io.ketill.IoDeviceSeeker;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.LibUsbException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * An I/O device seeker for USB devices, using LibUSB.
 * <p>
 * While this device seeker can be used for any device connected via USB,
 * users should be aware {@link HidDeviceSeeker} exists. When a USB device
 * is conformant to the HID protocol, the HID device seeker should be used.
 * The LibUSB device seeker exists mainly for devices which do not conform
 * to the HID protocol.
 * <p>
 * <b>Note:</b> Before calling {@link #seek()}, the device seeker must be
 * told which devices to seek out via {@link #seekProduct(int, int)}. If
 * this is neglected, an {@code IllegalStateException} will be thrown.
 *
 * @param <I> the I/O device type.
 * @param <L> the LibUSB device type. This can usually just be
 *            {@link LibUsbDevice}. However, if additional features
 *            of LibUSB must be implemented, extend that class and
 *            use it as the template type here instead.
 */
public abstract class LibUsbDeviceSeeker<I extends IoDevice,
        L extends LibUsbDevice> extends IoDeviceSeeker<I> {

    public static final int DEFAULT_SCAN_INTERVAL = 1000;

    private final LibUsbDeviceSupplier<L> deviceSupplier;
    private final Context usbContext;
    private final List<L> connected;
    private final List<DeviceInfo> seeking;
    private final List<Device> blacklisted;
    private final List<L> devices;
    private final int scanIntervalMs;
    private long lastScan;

    /**
     * @param scanIntervalMs the interval in milliseconds between USB
     *                       device enumeration scans.
     * @param deviceSupplier the LibUSB device supplier. Usually, this can
     *                       just be {@code LibUsbDevice::new}.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less
     *                                  than or equal to zero.
     * @throws NullPointerException     if {@code deviceSupplier}
     *                                  is {@code null}.
     * @throws LibUsbException          if LibUSB could not be initialized.
     */
    @SuppressWarnings("unchecked")
    public LibUsbDeviceSeeker(int scanIntervalMs,
                              @NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        if (scanIntervalMs <= 0) {
            throw new IllegalArgumentException("scanIntervalMs <= 0");
        }

        /*
         * While this is an unchecked cast, the template requires that the
         * type extend LibUsbDevice. As such, this cast is safe to perform.
         */
        Objects.requireNonNull(deviceSupplier, "deviceSupplier");
        this.deviceSupplier = (LibUsbDeviceSupplier<L>) deviceSupplier;

        this.usbContext = LibUsbDevice.initContext();

        this.connected = new ArrayList<>();
        this.seeking = new ArrayList<>();
        this.blacklisted = new ArrayList<>();
        this.devices = new ArrayList<>();
        this.scanIntervalMs = scanIntervalMs;
    }

    /**
     * Constructs a new {@code LibUsbDeviceSeeker} with the argument for
     * {@code scanIntervalMs} being {@value #DEFAULT_SCAN_INTERVAL}.
     *
     * @param deviceSupplier the LibUSB device supplier. Usually, this can
     *                       just be {@code LibUsbDevice::new}.
     * @throws NullPointerException if {@code deviceSupplier}
     *                              is {@code null}.
     * @throws LibUsbException      if LibUSB could not be initialized.
     */
    public LibUsbDeviceSeeker(@NotNull LibUsbDeviceSupplier<?
            extends LibUsbDevice> deviceSupplier) {
        this(DEFAULT_SCAN_INTERVAL, deviceSupplier);
    }

    public int getScanIntervalMs() {
        return this.scanIntervalMs;
    }

    /**
     * @param vendorId  the vendor ID.
     * @param productId the product ID.
     * @return {@code true} if this device seeker is seeking out USB devices
     * with {@code vendorId} and {@code productId}, {@code false} otherwise.
     * @throws IllegalArgumentException if {@code vendorId} or
     *                                  {@code productId} are not within range
     *                                  of {@code 0x0000} to {@code 0xFFFF}.
     */
    public boolean isSeekingProduct(int vendorId, int productId) {
        DeviceInfo.requireValidId(vendorId, productId);
        for (DeviceInfo info : seeking) {
            if (info.vendorId == vendorId && info.productId == productId) {
                return true;
            }
        }
        return false;
    }

    private boolean isSeekingProduct(@NotNull L device) {
        return this.isSeekingProduct(device.getVendorId(),
                device.getProductId());
    }

    private boolean isProduct(@NotNull L device, int vendorId, int productId) {
        return device.getVendorId() == vendorId
                && device.getProductId() == productId;
    }

    /**
     * Indicates to the seeker it should seek out USB devices with the
     * specified vendor and product ID. When such a device is located,
     * the {@link #onDeviceConnect(LibUsbDevice)} callback will be executed.
     *
     * @param vendorId  the vendor ID.
     * @param productId the product ID.
     * @throws IllegalArgumentException if {@code vendorId} or
     *                                  {@code productId} are not within range
     *                                  of {@code 0x0000} to {@code 0xFFFF}.
     * @throws IllegalStateException    if this HID device seeker has been
     *                                  closed via {@link #close()}.
     * @see #onDeviceConnect(LibUsbDevice)
     */
    protected void seekProduct(int vendorId, int productId) {
        DeviceInfo.requireValidId(vendorId, productId);
        this.requireOpen();
        if (!this.isSeekingProduct(vendorId, productId)) {
            seeking.add(new DeviceInfo(vendorId, productId));
        }
    }

    /**
     * Indicates to the seeker it should no longer seek out USB devices with
     * the specified vendor and product ID. All currently connected devices
     * with the specified vendor and product ID will be disconnected.
     *
     * @param vendorId  the vendor ID.
     * @param productId the product ID.
     * @throws IllegalArgumentException if {@code vendorId} or
     *                                  {@code productId} are not within range
     *                                  of {@code 0x0000} to {@code 0xFFFF}.
     * @throws IllegalStateException    if this HID device seeker has been
     *                                  closed via {@link #close()}.
     * @see #onDeviceDisconnect(LibUsbDevice)
     */
    protected void dropProduct(int vendorId, int productId) {
        DeviceInfo.requireValidId(vendorId, productId);
        this.requireOpen();
        if (!this.isSeekingProduct(vendorId, productId)) {
            return;
        }

        seeking.removeIf(info -> info.vendorId == vendorId
                && info.productId == productId);

        Iterator<L> devicesI = devices.iterator();
        while (devicesI.hasNext()) {
            L device = devicesI.next();
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
     * @param device the LibUSB device to blacklist.
     * @throws NullPointerException  if {@code handle} is {@code null}.
     * @throws IllegalStateException if this HID device seeker has been
     *                               closed via {@link #close()}.
     */
    protected void blacklistDevice(@NotNull L device) {
        Objects.requireNonNull(device, "device");
        this.requireOpen();
        Device usbDevice = device.getUsbDevice();
        if (!blacklisted.contains(usbDevice)) {
            blacklisted.add(usbDevice);
            this.disconnect(device);
        }
    }

    /**
     * Exempts a device from the blacklist. This can be used to allow a
     * previously blacklisted device to connect again.
     * <p>
     * <b>Note:</b> This method <i>does not</i> prevent a device from being
     * blacklisted again. To change the behavior of blacklisting, override
     * {@link #blacklistDevice(LibUsbDevice)}.
     *
     * @param device the LibUSB device to exempt.
     * @throws NullPointerException  if {@code device} is {@code null}.
     * @throws IllegalStateException if this HID device seeker has been
     *                               closed via {@link #close()}.
     */
    protected void exemptDevice(@NotNull LibUsbDevice device) {
        Objects.requireNonNull(device, "device");
        this.requireOpen();
        blacklisted.remove(device.getUsbDevice());
    }

    private void connect(@NotNull L device) {
        devices.add(device);

        /*
         * Only increment the reference count after the device
         * has been successfully opened. This prevents a memory
         * leak if the device fails to open. When the device is
         * disconnected, its reference count will be decreased.
         */
        try {
            this.onDeviceConnect(device);
            device.ref();
        } catch (Throwable cause) {
            device.unref();
            throw cause;
        }
    }

    private void disconnect(@NotNull L device) {
        devices.remove(device);

        /*
         * Surround onDeviceDisconnect() in a try catch block just
         * in case it throws an exception. This ensures that the
         * USB device is unreferenced even if the callback fails.
         */
        try {
            this.onDeviceDisconnect(device);
            device.unref();
        } catch (Throwable cause) {
            device.unref();
            throw cause;
        }
    }

    /* package-private for testing */
    synchronized void usbDeviceAttached(@NotNull L device) {
        if (devices.contains(device)) {
            /* don't unref(), it's being used */
            return;
        }

        Device usbDevice = device.getUsbDevice();
        if (blacklisted.contains(usbDevice)) {
            device.unref();
            return;
        }

        /*
         * Only unreference the device after determining it will
         * not be needed. When a device is found, attach() will
         * increase its reference count to keep it in memory.
         * Otherwise, free the device here.
         */
        if (!this.isSeekingProduct(device)) {
            device.unref();
            return;
        }

        try {
            device.open();
        } catch (Throwable cause) {
            device.unref();
            this.blacklistDevice(device);
            return;
        }

        this.connect(device);
    }

    /* package-private for testing */
    synchronized void usbDeviceDetached(@NotNull L device) {
        if (!devices.remove(device)) {
            return; /* device not previously connected */
        }
        this.disconnect(device);
    }

    /* package-private for testing */
    synchronized void scanDevices() {
        List<L> scanned =
                LibUsbDevice.getConnected(usbContext, deviceSupplier);

        for (L device : scanned) {
            if (!connected.contains(device)) {
                this.usbDeviceAttached(device);
                connected.add(device);
            }
        }

        Iterator<L> devicesI = connected.iterator();
        while (devicesI.hasNext()) {
            L device = devicesI.next();
            if (!scanned.contains(device)) {
                devicesI.remove();
                this.usbDeviceDetached(device);
                connected.remove(device);
            }
        }
    }

    /**
     * Called when a USB device being sought after has connected.
     * <p>
     * <b>Note:</b> Connected devices are <i>not</i> discovered.
     * They must be discovered using {@link #discoverDevice(IoDevice)}.
     *
     * @param device the LibUSB device.
     */
    protected abstract void onDeviceConnect(@NotNull L device);

    /**
     * Called when a previously connected USB device has disconnected.
     * <p>
     * <b>Note:</b> Disconnected devices are <i>not</i> forgotten.
     * They must be forgotten using {@link #forgetDevice(IoDevice)}.
     *
     * @param device the LibUSB device.
     */
    protected abstract void onDeviceDisconnect(@NotNull L device);

    @Override
    @MustBeInvokedByOverriders
    protected void seekImpl() {
        if (seeking.isEmpty()) {
            throw new IllegalStateException("no USB devices targeted");
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScan >= scanIntervalMs) {
            this.scanDevices();
            this.lastScan = currentTime;
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void close() {
        if (this.isClosed()) {
            return;
        }

        seeking.clear();
        blacklisted.clear();

        super.close();

        Iterator<L> devicesI = devices.iterator();
        while (devicesI.hasNext()) {
            L device = devicesI.next();
            device.close();
            devicesI.remove();
        }

        LibUsbDevice.exitContext(usbContext);
    }

}
