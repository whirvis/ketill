package io.ketill.hidusb;

import io.ketill.IoDevice;
import io.ketill.IoDeviceSeeker;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
 * told which devices to seek out via {@link #seek(int, int)}. If this is
 * neglected, an {@code IllegalStateException} will be thrown.
 *
 * @param <I> the I/O device type.
 */
public abstract class LibUsbDeviceSeeker<I extends IoDevice>
        extends IoDeviceSeeker<I> implements Closeable {

    public static final int DEFAULT_SCAN_INTERVAL = 1000;

    protected final @NotNull Context usbContext;

    private final List<DeviceInfo> seeking;
    private final List<Device> blacklisted;
    private final Map<Device, DeviceHandle> handles;
    private final int scanIntervalMs;
    private long lastScan;

    /**
     * @param scanIntervalMs the interval in milliseconds between USB
     *                       device enumeration scans.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less
     *                                  than or equal to zero.
     * @throws LibUsbException          if LibUSB could not be initialized.
     */
    public LibUsbDeviceSeeker(int scanIntervalMs) {
        if (scanIntervalMs <= 0) {
            throw new IllegalArgumentException("scanIntervalMs <= 0");
        }

        this.usbContext = new Context();
        int result = LibUsb.init(usbContext);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }

        this.seeking = new ArrayList<>();
        this.blacklisted = new ArrayList<>();
        this.handles = new HashMap<>();
        this.scanIntervalMs = scanIntervalMs;
    }

    /**
     * Constructs a new {@code LibUsbDeviceSeeker} with the argument for
     * {@code scanIntervalMs} being {@value #DEFAULT_SCAN_INTERVAL}.
     *
     * @throws LibUsbException if LibUSB could not be initialized.
     */
    public LibUsbDeviceSeeker() {
        this(DEFAULT_SCAN_INTERVAL);
    }

    /**
     * @param vendorId  the vendor ID.
     * @param productId the product ID.
     * @return {@code true} if this device seeker is seeking out USB devices
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

    private boolean isSeeking(@NotNull Device device) {
        DeviceDescriptor desc = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, desc);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }
        return this.isSeeking(desc.idVendor(), desc.idProduct());
    }

    private boolean isProduct(@NotNull Device device, int vendorId,
                              int productId) {
        DeviceDescriptor desc = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, desc);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }
        return desc.idVendor() == vendorId && desc.idProduct() == productId;
    }

    /**
     * Indicates to the seeker it should seek out USB devices with the
     * specified vendor and product ID. When such a device is located,
     * the {@link #onAttach(DeviceHandle)} callback will be executed.
     *
     * @param vendorId  the vendor ID.
     * @param productId the product ID.
     */
    protected void seek(int vendorId, int productId) {
        if (!this.isSeeking(vendorId, productId)) {
            seeking.add(new DeviceInfo(vendorId, productId));
        }
    }

    /**
     * Indicates to the seeker it should no longer seek out USB devices with
     * the specified vendor and product ID. All currently attached devices
     * with the specified vendor and product ID will be detached.
     *
     * @param vendorId  the vendor ID.
     * @param productId the product ID.
     * @see #onDetach(DeviceHandle)
     */
    protected void drop(int vendorId, int productId) {
        if (!this.isSeeking(vendorId, productId)) {
            return;
        }

        Iterator<Device> devicesI = handles.keySet().iterator();
        while (devicesI.hasNext()) {
            Device device = devicesI.next();
            DeviceHandle handle = handles.get(device);
            if (this.isProduct(device, vendorId, productId)) {
                devicesI.remove();
                this.detach(handle);
            }
        }
    }

    /**
     * Blacklists a device from this device seeker. When blacklisted, a device
     * will be forcefully detached. Afterwards it will not be reattached.
     *
     * @param handle the handle of the USB device to blacklist.
     * @throws NullPointerException     if {@code handle} is {@code null}.
     * @throws IllegalArgumentException if {@code handle} does not belong
     *                                  to a device handled by this device
     *                                  seeker's LibUSB context.
     */
    protected void blacklist(@NotNull DeviceHandle handle) {
        Objects.requireNonNull(handle, "handle");
        Device device = LibUsb.getDevice(handle);
        if (device == null) {
            throw new IllegalArgumentException("no such device");
        }

        if (!blacklisted.contains(device)) {
            blacklisted.add(device);
            this.detach(handle);
        }
    }

    private void attach(@NotNull Device device) {
        DeviceHandle handle = new DeviceHandle();
        int result = LibUsb.open(device, handle);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }

        /*
         * Only increment the reference count for the device after it has
         * been successfully opened. This will prevent a memory leak if a
         * device fails to open. The reference count will subsequently be
         * decreased when the device is detached.
         */
        LibUsb.refDevice(device);

        handles.put(device, handle);
        this.onAttach(handle);
    }

    private void detach(@NotNull DeviceHandle handle) {
        Device device = LibUsb.getDevice(handle);
        handles.remove(device);

        /*
         * Now that the device has been detached, its reference count must
         * be decreased (as it was increased back when first attached.) If
         * this is not done, a memory leak will soon follow.
         */
        try {
            this.onDetach(handle);
            LibUsb.unrefDevice(device);
        } catch (RuntimeException e) {
            LibUsb.unrefDevice(device);
            throw e;
        }
    }

    private void scanDevices() {
        DeviceList devices = new DeviceList();
        int result = LibUsb.getDeviceList(usbContext, devices);
        if (result < 0) { /* result is device count */
            throw new LibUsbException(result);
        }

        List<Device> connected = new ArrayList<>();
        for (Device device : devices) {
            connected.add(device);

            if (handles.containsKey(device)) {
                continue;
            }

            if (blacklisted.contains(device)) {
                LibUsb.unrefDevice(device);
                continue;
            }

            if (this.isSeeking(device)) {
                this.attach(device);
            } else {
                /*
                 * Only unreference the device after determining it will
                 * not be needed. When a device is found, attach() will
                 * increase its reference count to keep it in memory.
                 * Otherwise, free the device here.
                 */
                LibUsb.unrefDevice(device);
            }
        }

        Iterator<DeviceHandle> handlesI = handles.values().iterator();
        while (handlesI.hasNext()) {
            DeviceHandle handle = handlesI.next();
            Device device = LibUsb.getDevice(handle);
            if (!connected.contains(device)) {
                handlesI.remove();
                this.detach(handle);
            }
        }

        /*
         * Now that the devices have been transferred to garbage collected
         * memory, free the list handle (but keep the device handles.) The
         * argument for the second parameter must be false. If set to true,
         * the device handles will be erroneously freed.
         */
        LibUsb.freeDeviceList(devices, false);
    }

    /**
     * Called when a USB device being sought after has been attached.
     * <p>
     * <b>Note:</b> Attached devices are <i>not</i> discovered.
     * They must be discovered using {@link #discoverDevice(IoDevice)}.
     *
     * @param handle the device handle.
     * @see #seek(int, int)
     */
    protected abstract void onAttach(@NotNull DeviceHandle handle);

    /**
     * Called when a previously attached USB device has been detached.
     * <p>
     * <b>Note:</b> Detached devices are <i>not</i> forgotten.
     * They must be forgotten using {@link #forgetDevice(IoDevice)}.
     *
     * @param handle the device handle.
     * @see #blacklist(DeviceHandle)
     */
    protected abstract void onDetach(@NotNull DeviceHandle handle);

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
        Iterator<DeviceHandle> handlesI = handles.values().iterator();
        while (handlesI.hasNext()) {
            DeviceHandle handle = handlesI.next();
            LibUsb.close(handle);
            handlesI.remove();
        }
        LibUsb.exit(usbContext);
    }

}
