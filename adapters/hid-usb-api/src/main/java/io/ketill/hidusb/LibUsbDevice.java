package io.ketill.hidusb;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usb4java.*;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A wrapper for an underlying LibUSB device.
 * <p>
 * This class was created to make unit testing possible with Mockito (which
 * does not support mocking native methods.) However, it has proven to make
 * the code which interfaces with USB devices cleaner and safer.
 * <p>
 * This class can be extended and provided as the LibUSB device template to
 * a {@link LibUsbDeviceSeeker}. Children of this class have access to the
 * underlying USB context, device, handle, etc. These can be used to add
 * missing LibUSB functionality.
 *
 * @see LibUsbDeviceSupplier
 */
public class LibUsbDevice implements Closeable {

    /**
     * Initializes a LibUSB context.
     *
     * @return the initialized context.
     * @throws LibUsbException if an error code is returned when
     *                         initializing the context.
     * @see #exitContext(Context)
     */
    public static @NotNull Context initContext() {
        Context context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }
        return context;
    }

    /**
     * Shuts down a LibUSB context.
     *
     * @param context the context to de-initialize. A value of
     *                {@code null} is <i>not</i> permitted, the
     *                default context is forbidden.
     * @throws NullPointerException if {@code context} is {@code null}.
     */
    public static void exitContext(@NotNull Context context) {
        /*
         * Although LibUSB accepts a null for the default context,
         * this method is used to de-initialize a context created
         * by initContext() (which never returns a null value.) As
         * such, it is assumed to be an error by the user if they
         * provide null context.
         */
        Objects.requireNonNull(context, "context");
        LibUsb.exit(context);
    }

    /**
     * Returns all USB devices currently connected to the system.
     * <p>
     * All devices returned in this list must be freed, otherwise a memory
     * leak will occur. LibUSB devices can be freed by de-referencing them
     * via {@link #unref()} until their reference count reaches zero. It
     * is recommended to use {@link #close()} when finished with a LibUSB
     * device, as it will ensure it is freed from memory.
     *
     * @param context        the context to operate on. A value of
     *                       {@code null} is <i>not</i> permitted,
     *                       the default context is forbidden.
     * @param deviceSupplier the LibUSB device supplier.
     * @throws NullPointerException if {@code context} or
     *                              {@code deviceSupplier} are {@code null};
     *                              if a LibUSB device given by
     *                              {@code deviceSupplier} is {@code null}.
     * @throws LibUsbException      if an error code is returned when
     *                              retrieving the device list.
     * @see #closeDevices(Iterable)
     */
    /* @formatter:off */
    public static @NotNull <L extends LibUsbDevice> List<@NotNull L>
            getConnected(@NotNull Context context,
                         @NotNull LibUsbDeviceSupplier<L> deviceSupplier) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(deviceSupplier, "deviceSupplier");

        DeviceList devices = new DeviceList();
        int result = LibUsb.getDeviceList(context, devices);
        if (result < 0) { /* result is device count */
            throw new LibUsbException(result);
        }

        List<L> connected = new ArrayList<>();
        for (Device device : devices) {
            L wrapped = deviceSupplier.get(context, device);
            Objects.requireNonNull(wrapped, "supplied device is null");
            connected.add(wrapped);
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

    /**
     * @param devices the devices to close.
     * @throws NullPointerException if {@code devices} is {@code null}.
     */
    /* @formatter:off */
    public static void
            closeDevices(@NotNull Iterable<? extends LibUsbDevice> devices) {
        Objects.requireNonNull(devices, "devices");
        for (LibUsbDevice device : devices) {
            Objects.requireNonNull(device, "device");
            device.close();
        }
    }
    /* @formatter:on */

    private final @NotNull Context usbContext;
    private final @NotNull Device usbDevice;
    private int refCount;

    protected final @NotNull DeviceDescriptor usbDescriptor;
    private final int vendorId; /* follow getter pattern */
    private final int productId; /* follow getter pattern */

    private @Nullable DeviceHandle usbHandle;
    private boolean closed;

    /**
     * Preferably, the construction of LibUSB devices should only be performed
     * by {@link #getConnected(Context, LibUsbDeviceSupplier)}. If this
     * <i>must</i> be used, responsibility is placed upon the caller to
     * provide the correct arguments.
     * <p>
     * Children underlying USB context and USB device can be accessed via
     * the internal {@code usbContext} and {@code usbDevice} fields. The
     * device descriptor is also available via {@code usbDescriptor}.
     *
     * @param context  the context to operate on. A value of
     *                 {@code null} is <i>not</i> permitted,
     *                 the default context is forbidden.
     * @param device   the USB device to perform I/O on.
     * @param refCount the current reference count of {@code device}.
     *                 <b>This must be accurate, or {@link #ref()} and
     *                 {@link #unref()} will not function correctly</b>.
     * @throws NullPointerException if {@code context} or {@code device}
     *                              are {@code null}.
     * @throws LibUsbException      if an error code is returned when
     *                              getting the device descriptor.
     * @see #close()
     */
    public LibUsbDevice(@NotNull Context context, @NotNull Device device,
                        int refCount) {
        this.usbContext = Objects.requireNonNull(context, "context");
        this.usbDevice = Objects.requireNonNull(device, "device");
        this.refCount = refCount;

        this.usbDescriptor = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, usbDescriptor);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }

        /*
         * Vendor IDs and product IDs are unsigned shorts. However,
         * the underlying LibUSB API returns them as a signed Java
         * short. This converts them to an unsigned value and stores
         * them in an int so the expected value is returned.
         */
        this.vendorId = usbDescriptor.idVendor() & 0xFFFF;
        this.productId = usbDescriptor.idProduct() & 0xFFFF;
    }

    /**
     * Preferably, the construction of LibUSB devices should only be performed
     * by {@link #getConnected(Context, LibUsbDeviceSupplier)}. If this
     * <i>must</i> be used, responsibility is placed upon the caller to
     * provide the correct arguments.
     * <p>
     * Children underlying USB context and USB device can be accessed via
     * the internal {@code usbContext} and {@code usbDevice} fields. The
     * device descriptor is also available via {@code usbDescriptor}.
     *
     * @param context the context to operate on. A value of
     *                {@code null} is <i>not</i> permitted,
     *                the default context is forbidden.
     * @param device  the USB device to perform I/O on.
     *                <b>The reference count of this device
     *                is assumed to be equal to one.</b>
     * @throws NullPointerException if {@code context} or {@code device}
     *                              are {@code null}.
     * @throws LibUsbException      if an error code is returned when
     *                              getting the device descriptor.
     * @see #close()
     */
    public LibUsbDevice(@NotNull Context context, @NotNull Device device) {
        this(context, device, 1);
    }

    /* getter for testing */
    protected final @NotNull Context usbContext() {
        return this.usbContext;
    }

    /* getter for testing */
    protected final @NotNull Device usbDevice() {
        return this.usbDevice;
    }

    /**
     * @return the vendor ID of this device as an {@code unsigned short}.
     */
    public final int getVendorId() {
        return this.vendorId;
    }

    /**
     * @return the product ID of this device as an {@code unsigned short}.
     */
    public final int getProductId() {
        return this.productId;
    }

    /**
     * Increments the reference count of this device.
     *
     * @see #unref()
     */
    protected final void ref() {
        this.requireOpen();
        LibUsb.refDevice(usbDevice);
        this.refCount++;
    }

    /**
     * Decrements the reference count of this device.
     * <p>
     * If this decrement operation causes the internal reference count to
     * reach zero, the underlying LibUSB device will be destroyed and
     * {@link #close()} will be called automatically.
     *
     * @see #ref()
     */
    protected final void unref() {
        this.requireOpen();
        LibUsb.unrefDevice(usbDevice);
        this.refCount--;
        if (refCount <= 0) {
            this.close();
        }
    }

    /**
     * @return the USB device handle.
     * @throws IllegalStateException if a call to {@link #open()} was not
     *                               made before calling this method.
     */
    protected final @NotNull DeviceHandle usbHandle() {
        if (usbHandle == null) {
            throw new IllegalStateException("handle not open");
        }
        return this.usbHandle;
    }

    /**
     * Opens this device and obtains an internal device handle. This USB
     * handle can be obtained via {@link #usbHandle()}. If the handle is
     * already open, this method has no effect.
     * <p>
     * <b>Note:</b> A LibUSB device can be closed without ever opening a
     * handle. The method {@link #requireOpen()} only ensures that this
     * device has not been closed via {@link #close()}.
     *
     * @throws LibUsbException if an error code is returned when opening
     *                         the device handle.
     */
    protected final void open() {
        if (usbHandle != null) {
            return;
        }
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

    public final boolean isClosed() {
        return this.closed;
    }

    /**
     * Destroys the internal LibUSB device. If this device was opened via
     * {@link #open()}, the internal LibUSB handle is also closed. If the
     * device is already closed then invoking this method has no effect.
     */
    @Override
    @MustBeInvokedByOverriders
    public void close() {
        if (closed) {
            return;
        }

        this.refCount = 0;

        if (usbHandle != null) {
            LibUsb.close(usbHandle);
            this.usbHandle = null;
        }

        this.closed = true;
    }

}
