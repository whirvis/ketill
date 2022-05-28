package io.ketill.hidusb;

import org.jetbrains.annotations.NotNull;
import org.usb4java.Context;
import org.usb4java.Device;

/**
 * A supplier of a {@link LibUsbDevice}, used by {@link LibUsbDeviceSeeker}
 * when instantiating the wrappers for devices it's observed while scanning.
 * This allows for custom wrappers, which implement extra functionality of
 * LibUSB if necessary.
 *
 * @param <L> the LibUSB device type.
 */
@FunctionalInterface
public interface LibUsbDeviceSupplier<L extends LibUsbDevice> {

    /**
     * Gets a LibUSB device.
     *
     * @param context the context which LibUSB is operating on.
     * @param device  the underlying LibUSB device to perform I/O on.
     * @return the LibUSB device.
     */
    @NotNull L get(@NotNull Context context, @NotNull Device device);

}
