package io.ketill.hidusb.gc;

import io.ketill.hidusb.LibUsbDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.Transfer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

final class LibUsbDeviceGc extends LibUsbDevice {

    private final Map<Transfer, Consumer<ByteBuffer>> pendingTransfers;

    LibUsbDeviceGc(@NotNull Context context, @NotNull Device device) {
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

    @SuppressWarnings("SameParameterValue")
    void fillInterruptTransfer(Transfer transfer, byte endpoint,
                               ByteBuffer buffer,
                               Consumer<ByteBuffer> callback,
                               Object userData, long timeout) {
        this.requireOpen();
        LibUsb.fillInterruptTransfer(transfer, this.getHandle(), endpoint,
                buffer, this::processTransfer, userData, timeout);
        pendingTransfers.put(transfer, callback);
    }

    void interruptTransfer(byte endpoint, ByteBuffer data,
                           @Nullable IntBuffer transferred, long timeout) {
        this.requireOpen();
        requireSuccess(() -> LibUsb.interruptTransfer(getHandle(), endpoint,
                data, transferred, timeout));
    }

    void submitTransfer(Transfer transfer) {
        this.requireOpen();
        requireSuccess(() -> LibUsb.submitTransfer(transfer));
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

}
