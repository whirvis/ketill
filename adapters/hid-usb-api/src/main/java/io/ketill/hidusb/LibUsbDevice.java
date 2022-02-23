package io.ketill.hidusb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TODO: docs
 */
public class LibUsbDevice implements Closeable {

    /**
     * TODO: docs
     */
    public static Context initContext() {
        Context context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }
        return context;
    }

    /**
     * TODO: docs
     */
    public static void exitContext(Context context) {
        LibUsb.exit(context);
    }

    /**
     * TODO: docs
     */
    /* @formatter:off */
    public static <L extends LibUsbDevice> List<L>
            getConnected(Context context,
                         @NotNull LibUsbDeviceSupplier<L> deviceSupplier) {
        DeviceList devices = new DeviceList();
        int result = LibUsb.getDeviceList(context, devices);
        if (result < 0) { /* result is device count */
            throw new LibUsbException(result);
        }

        List<L> connected = new ArrayList<>();
        for (Device device : devices) {
            connected.add(deviceSupplier.get(context, device));
        }

        /*
         * Now that the devices have been transferred to garbage
         * collected memory, free the list handle (but keep each
         * device handle.) The argument for the second parameter
         * must be false. If set to true, the device handles will
         * be erroneously freed.
         */
        LibUsb.freeDeviceList(devices, false);

        return connected;
    }
    /* @formatter:on */

    private final @NotNull Context usbContext;
    private final @NotNull Device usbDevice;
    private final @NotNull DeviceDescriptor usbDescriptor;
    private final int vendorId;
    private final int productId;
    private @Nullable DeviceHandle usbHandle;
    private boolean closed;

    private int refCount;

    /**
     * TODO: docs
     */
    public LibUsbDevice(@NotNull Context context, @NotNull Device device) {
        this.usbContext = Objects.requireNonNull(context, "context");
        this.usbDevice = Objects.requireNonNull(device, "device");

        this.usbDescriptor = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, usbDescriptor);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }

        /*
         * Vendor IDs and product IDs are unsigned shorts. However, the
         * underlying LibUSB API returns them as a signed Java short. This
         * converts them to  an unsigned value and stores them in an int so
         * the expected value is returned when queried.
         */
        this.vendorId = usbDescriptor.idVendor() & 0xFFFF;
        this.productId = usbDescriptor.idProduct() & 0xFFFF;

        /*
         * TODO: explanation
         */
        this.refCount = 1;
    }

    /**
     * TODO: docs
     */
    protected final @NotNull Context getUsbContext() {
        return this.usbContext;
    }

    /**
     * TODO: docs
     */
    protected final @NotNull Device getUsbDevice() {
        return this.usbDevice;
    }

    /**
     * TODO: docs
     */
    protected final @NotNull DeviceDescriptor getUsbDescriptor() {
        return this.usbDescriptor;
    }

    /**
     * TODO: docs
     */
    protected final int getVendorId() {
        return this.vendorId;
    }

    /**
     * TODO: docs
     */
    protected final int getProductId() {
        return this.productId;
    }

    /**
     * TODO: docs
     */
    protected @Nullable DeviceHandle getHandle() {
        return this.usbHandle;
    }

    /**
     * TODO: docs
     */
    protected void ref() {
        this.requireOpen();
        LibUsb.refDevice(usbDevice);
        this.refCount++;
    }

    /**
     * TODO: docs
     */
    /* TODO: finish up */
    protected void unref() {
        this.requireOpen();
        LibUsb.unrefDevice(usbDevice);
        this.refCount--;
        if (refCount <= 0) {
            this.close();
        }
    }

    /* TODO: finish up */
    void open() {
        this.usbHandle = new DeviceHandle();
        int result = LibUsb.open(usbDevice, usbHandle);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }
    }

    /**
     * @throws IllegalStateException if this LibUSB device has been
     *                               closed via {@link #close()}.
     */
    protected final void requireOpen() {
        if (closed) {
            String refCountStr = " (refCount: " + refCount + ")";
            throw new IllegalStateException("device closed" + refCountStr);
        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    /* TODO: finish up */
    @Override
    public void close() {
        if (closed) {
            return;
        }

        if(usbHandle != null) {
            LibUsb.close(usbHandle);
            this.usbHandle = null;
        }

        this.refCount = 0;
        this.closed = true;
    }

}
