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
 * @see #requireSuccess(LibUsbOperation)
 */
public class LibUsbDevice implements Closeable {

    protected interface LibUsbOperation {
        int execute();
    }

    /**
     * Requires that an operation return a value indicating a LibUSB
     * operation was successful. Intended to reduce boilerplate when
     * making calls to methods in the {@code LibUsb} class.
     *
     * @param operation the code to execute.
     * @return the result of the operation, can be ignored.
     * @throws NullPointerException if {@code operation} is {@code null}.
     * @throws LibUsbException if an error code is returned.
     */
    @SuppressWarnings("UnusedReturnValue")
    protected static int requireSuccess(@NotNull LibUsbOperation operation) {
        Objects.requireNonNull(operation, "operation");
        int result = operation.execute();
        if (result < LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }
        return result;
    }

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
        requireSuccess(() -> LibUsb.init(context));
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
     * leak will occur. Use {@link #close()} when finished with a LibUSB
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
        requireSuccess(() -> LibUsb.getDeviceList(context, devices));

        List<L> connected = new ArrayList<>();
        for (Device device : devices) {
            L wrapped = deviceSupplier.get(context, device);
            Objects.requireNonNull(wrapped, "supplied device is null");
            connected.add(wrapped);
            break;
        }

        /*
         * Now that the devices have been transferred to garbage
         * collected memory, free the list handle (but keep each
         * device handle.) The argument for the second parameter
         * must be false. When true, it decreases the reference
         * count by one. That would release them from memory too
         * early in this situation.
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
     * @throws NullPointerException if {@code context} or {@code device}
     *                              are {@code null}.
     * @throws LibUsbException      if an error code is returned when
     *                              getting the device descriptor.
     * @see #close()
     */
    public LibUsbDevice(@NotNull Context context, @NotNull Device device) {
        this.usbContext = Objects.requireNonNull(context, "context");
        this.usbDevice = Objects.requireNonNull(device, "device");

        this.usbDescriptor = new DeviceDescriptor();
        requireSuccess(() -> LibUsb.getDeviceDescriptor(device,
                usbDescriptor));

        /*
         * Vendor IDs and product IDs are unsigned shorts. However,
         * the underlying LibUSB API returns them as a signed Java
         * short. This converts them to an unsigned value and stores
         * them in an int so the expected value is returned.
         */
        this.vendorId = usbDescriptor.idVendor() & 0xFFFF;
        this.productId = usbDescriptor.idProduct() & 0xFFFF;
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
     * @return the USB device handle.
     * @throws IllegalStateException if a call to {@link #openHandle()} was not
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
    protected final void openHandle() {
        this.requireOpen();
        if (usbHandle != null) {
            return;
        }
        this.usbHandle = new DeviceHandle();
        requireSuccess(() -> LibUsb.open(usbDevice, usbHandle));
    }

    /**
     * @throws IllegalStateException if this LibUSB device has been
     *                               closed via {@link #close()}.
     */
    protected final void requireOpen() {
        if (closed) {
            throw new IllegalStateException("device closed");
        }
    }

    public final boolean isClosed() {
        return this.closed;
    }

    /**
     * Destroys the internal LibUSB device. If this device was opened via
     * {@link #openHandle()}, the internal LibUSB handle is also closed.
     * If the device is already closed then invoking this method has no
     * effect.
     */
    @Override
    @MustBeInvokedByOverriders
    public void close() {
        if (closed) {
            return;
        }

        LibUsb.unrefDevice(usbDevice);

        if (usbHandle != null) {
            LibUsb.close(usbHandle);
            this.usbHandle = null;
        }

        this.closed = true;
    }

}
