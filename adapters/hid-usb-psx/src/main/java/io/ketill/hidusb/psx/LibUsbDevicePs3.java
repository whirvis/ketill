package io.ketill.hidusb.psx;

import io.ketill.hidusb.LibUsbDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.Transfer;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

final class LibUsbDevicePs3 extends LibUsbDevice {

    private final Map<Transfer, Consumer<ByteBuffer>> pendingTransfers;

    LibUsbDevicePs3(@NotNull Context context, @NotNull Device device) {
        super(context, device);
        this.pendingTransfers = new HashMap<>();
    }

    private void processTransfer(@NotNull Transfer transfer) {
        DeviceHandle handle = this.getHandle();
        if (!transfer.devHandle().equals(handle)) {
            return; /* not our device */
        }

        Consumer<ByteBuffer> onTransfer = pendingTransfers.remove(transfer);
        if (onTransfer != null) {
            onTransfer.accept(transfer.buffer());
        }

        LibUsb.freeTransfer(transfer);
    }

    void fillInterruptTransfer(@NotNull Transfer transfer, byte endpoint,
                               @NotNull ByteBuffer buffer,
                               @NotNull Consumer<ByteBuffer> callback,
                               @Nullable Object userData, long timeout) {
        this.requireOpen();
        LibUsb.fillInterruptTransfer(transfer, this.getHandle(), endpoint,
                buffer, this::processTransfer, userData, timeout);
        pendingTransfers.put(transfer, callback);
    }

    @SuppressWarnings("SameParameterValue")
    void handleEventsTimeout(long timeout) {
        this.requireOpen();
        requireSuccess(() -> LibUsb.handleEventsTimeout(usbContext, timeout));
    }

    void claimInterface(byte iface) {
        this.requireOpen();
        requireSuccess(() -> LibUsb.claimInterface(this.getHandle(), iface));
    }

    void controlTransfer(byte requestType, byte request, short value,
                         short index, ByteBuffer data, long timeout) {
        this.requireOpen();
        requireSuccess(() -> LibUsb.controlTransfer(this.getHandle(),
                requestType, request, value, index, data, timeout));
    }

}
