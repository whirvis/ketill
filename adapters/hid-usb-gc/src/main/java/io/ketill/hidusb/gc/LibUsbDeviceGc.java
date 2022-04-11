package io.ketill.hidusb.gc;

import io.ketill.hidusb.LibUsbDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

final class LibUsbDeviceGc extends LibUsbDevice {

    private final Map<Transfer, Consumer<ByteBuffer>> pendingTransfers;

    LibUsbDeviceGc(@NotNull Context context, @NotNull Device usbDevice) {
        super(context, usbDevice);
        this.pendingTransfers = new HashMap<>();
    }

    private void processTransfer(@NotNull Transfer transfer) {
        DeviceHandle handle = this.getHandle();
        if (!transfer.devHandle().equals(handle)) {
            return; /* not this device */
        }

        Consumer<ByteBuffer> transferCallback =
                pendingTransfers.remove(transfer);
        if (transferCallback != null) {
            transferCallback.accept(transfer.buffer());
        }

        LibUsb.freeTransfer(transfer);
    }

    /**
     * Populates the required fields of a {@link Transfer} instance.
     *
     * @param transfer the transfer to populate.
     * @param endpoint the address of the endpoint.
     * @param buffer   the data buffer to transfer.
     * @param callback the method to invoke on transfer completion. A value
     *                 of {@code null} is permitted, and will result in no
     *                 code being executed on transfer completion.
     * @param userData user data to pass to callback method. A value of
     *                 {@code null} is permitted, and will have no user
     *                 data passed to the callback method.
     * @param timeout  the transfer timeout specified in milliseconds.
     * @throws NullPointerException     if {@code transfer} or {@code buffer}
     *                                  are {@code null}.
     * @throws IllegalArgumentException if {@code timeout} is negative.
     * @throws IllegalStateException    if this LibUSB device has been
     *                                  closed via {@link #close()}.
     */
    @SuppressWarnings("SameParameterValue")
    void fillInterruptTransfer(@NotNull Transfer transfer, byte endpoint,
                               @NotNull ByteBuffer buffer,
                               @Nullable Consumer<ByteBuffer> callback,
                               @Nullable Object userData, long timeout) {
        Objects.requireNonNull(transfer, "transfer cannot be null");
        Objects.requireNonNull(buffer, "buffer cannot be null");
        if (timeout < 0L) {
            throw new IllegalArgumentException("timeout cannot be negative");
        }
        this.requireOpen();

        LibUsb.fillInterruptTransfer(transfer, this.getHandle(), endpoint,
                buffer, this::processTransfer, userData, timeout);
        if (callback != null) {
            pendingTransfers.put(transfer, callback);
        }
    }

    /**
     * Perform a USB interrupt transfer. The direction of the transfer is
     * inferred from the direction bits of the endpoint address. Furthermore,
     * the default value is used as the polling interval.
     * <p>
     * For both reading and writing, fewer than expected bytes may be read
     * or written from/to the device. As such, be sure to check the return
     * value to ensure the needed data was transferred.
     *
     * @param endpoint the address of the endpoint.
     * @param data     the data buffer to transfer.
     * @param timeout  the amount of time in milliseconds this method
     *                 should wait before giving up due to no response
     *                 being received. For an unlimited timeout, use a
     *                 value of zero.
     * @return the amount of bytes actually transferred.
     * @throws NullPointerException     if {@code data} is {@code null}.
     * @throws IllegalArgumentException if {@code timeout} is negative.
     * @throws IllegalStateException    if this LibUSB device has been
     *                                  closed via {@link #close()}.
     * @throws LibUsbException          if an error code is returned while
     *                                  completing the interrupt transfer.
     */
    @SuppressWarnings("UnusedReturnValue")
    int interruptTransfer(byte endpoint, @NotNull ByteBuffer data,
                          long timeout) {
        Objects.requireNonNull(data, "data cannot be null");
        if (timeout < 0L) {
            throw new IllegalArgumentException("timeout cannot be negative");
        }
        this.requireOpen();

        IntBuffer transferred = IntBuffer.allocate(1);
        requireSuccess(() -> LibUsb.interruptTransfer(getHandle(), endpoint,
                data, transferred, timeout));
        return transferred.get();
    }

    /**
     * @param transfer the transfer to submit.
     * @throws NullPointerException if {@code transfer} is {@code null}.
     * @throws LibUsbException      if an error code is returned while
     *                              submitting
     *                              the transfer.
     */
    void submitTransfer(@NotNull Transfer transfer) {
        Objects.requireNonNull(transfer, "transfer cannot be null");
        this.requireOpen();
        requireSuccess(() -> LibUsb.submitTransfer(transfer));
    }

    /**
     * Handles any pending events. LibUSB determines "pending events" by
     * checking if any timeouts have expired and by checking the set of
     * file descriptors for activity.
     * <p>
     * If the argument for {@code timeout} is zero, this function will
     * handle any currently pending events and then immediately return
     * in non-blocking style.
     * <p>
     * If the argument for {@code timeout} is not zero and no events are
     * currently pending, this function will block waiting for events to
     * handle up until the specified timeout. If an event arrives or a
     * signal is raised, this function will return early.
     *
     * @param timeout the maximum time in microseconds to block waiting for
     *                events, or zero for non-blocking mode.
     * @return the amount of completed events.
     * @throws IllegalArgumentException if {@code timeout} is negative.
     * @throws LibUsbException          if an error code is returned while
     *                                  handling the completed events.
     */
    @SuppressWarnings({"SameParameterValue", "UnusedReturnValue"})
    int handleEventsCompleted(long timeout) {
        if (timeout < 0L) {
            throw new IllegalArgumentException("timeout cannot be negative");
        }
        this.requireOpen();

        IntBuffer completed = IntBuffer.allocate(1);
        requireSuccess(() -> LibUsb.handleEventsTimeoutCompleted(usbContext,
                timeout, completed));
        return completed.get();
    }

    /**
     * Claims an interface on a given device handle. Interfaces must be
     * claimed before I/O can be performed on any of their endpoints
     * Furthermore, Attempting to claim an already claimed interface
     * is legal. In this case, LibUSB does nothing.
     * <p>
     * <b>Note:</b> Claiming interfaces is a purely logical operation. It
     * will cause no requests to be sent over the bus. Interface claiming
     * is used to instruct the underlying operating system an application
     * wishes to take ownership of the interface.
     *
     * @param number the number of the interface to claim.
     * @throws LibUsbException if an error code is returned while
     *                         attempting to claim the interface.
     */
    void claimInterface(byte number) {
        this.requireOpen();
        requireSuccess(() -> LibUsb.claimInterface(this.getHandle(), number));
    }

    @Override
    public void close() {
        if (this.isClosed()) {
            return;
        }
        super.close();
        pendingTransfers.clear();
    }

}
