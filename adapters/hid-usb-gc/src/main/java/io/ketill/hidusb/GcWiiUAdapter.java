package io.ketill.hidusb;

import io.ketill.AdapterSupplier;
import io.ketill.gc.GcController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usb4java.Context;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;
import org.usb4java.TransferCallback;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

/**
 * TODO: docs
 */
public final class GcWiiUAdapter implements TransferCallback, Closeable {

    /* @formatter:off */
    private static final byte
            CONFIG           = (byte) 0x00,
            ENDPOINT_IN      = (byte) 0x81,
            ENDPOINT_OUT     = (byte) 0x02;

    private static final byte
            RUMBLE_ID        = (byte) 0x11,
            INIT_ID          = (byte) 0x13,
            DATA_ID          = (byte) 0x21;

    private static final byte
            RUMBLE_STOP      = (byte) 0x00,
            RUMBLE_START     = (byte) 0x01;
         /* RUMBLE_STOP_HARD = (byte) 0x02; */

    private static final int
            DATA_LEN         = 37;
    /* @formatter:on */

    public static final int SLOT_COUNT = 4;

    private final Context usbContext;
    private final DeviceHandle usbHandle;
    private final GcWiiUSlotState[] slots;
    private final ByteBuffer rumblePacket;

    private boolean initializedAdapter;
    private boolean requestedData;
    private boolean closed;

    /**
     * TODO: docs
     *
     * @param usbContext the LibUSB context, may be {@code null}.
     * @param usbHandle  the USB handle.
     * @throws NullPointerException if {@code usbHandle} is {@code} null.
     */
    public GcWiiUAdapter(@Nullable Context usbContext,
                         @NotNull DeviceHandle usbHandle) {
        this.usbContext = usbContext;
        this.usbHandle = Objects.requireNonNull(usbHandle, "usbHandle");

        this.slots = new GcWiiUSlotState[SLOT_COUNT];
        for (int i = 0; i < slots.length; i++) {
            this.slots[i] = new GcWiiUSlotState();
        }

        /*
         * The size of this packet is equal to one (the rumble packet
         * ID) plus the amount of slots present on this adapter. Each
         * slot takes one byte for its rumble status.
         */
        this.rumblePacket = ByteBuffer.allocateDirect(1 + slots.length);
        rumblePacket.put(RUMBLE_ID);
    }

    /**
     * @param port the port of the slot to check.
     * @return {@code true} if a GameCube controller is connected to the
     * specified port, {@code} false otherwise.
     * @throws IndexOutOfBoundsException if {@code port} is less than
     *                                   zero or greater than or equal
     *                                   to {@value #SLOT_COUNT}.
     * @throws IllegalStateException     if the adapter is closed.
     * @see #getSlotSupplier(int)
     */
    public boolean isSlotConnected(int port) {
        if (port < 0 || port >= slots.length) {
            throw new IndexOutOfBoundsException("no such port " + port);
        } else if (closed) {
            throw new IllegalStateException("adapter closed");
        }
        return slots[port].isConnected();
    }

    /**
     * @param port the port of the slot.
     * @return the adapter supplier for a GameCube controller connected to
     * the specified port. The same instance is returned on each invocation.
     * @throws IndexOutOfBoundsException if {@code port} is less than
     *                                   zero or greater than or equal
     *                                   to {@value #SLOT_COUNT}.
     * @throws IllegalStateException     if the adapter is closed.
     * @see #isSlotConnected(int)
     */
    public @NotNull AdapterSupplier<GcController> getSlotSupplier(int port) {
        if (port < 0 || port >= slots.length) {
            throw new IndexOutOfBoundsException("no such port " + port);
        } else if (closed) {
            throw new IllegalStateException("adapter closed");
        }
        return slots[port].supplier;
    }

    private void initAdapter() {
        int result = LibUsb.claimInterface(usbHandle, CONFIG);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }

        ByteBuffer init = ByteBuffer.allocateDirect(1);
        init.put(INIT_ID);

        IntBuffer transferred = IntBuffer.allocate(1);
        result = LibUsb.interruptTransfer(usbHandle, ENDPOINT_OUT, init,
                transferred, 0);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }
    }

    private void sendRumblePacket() {
        boolean submit = false;

        for (int i = 0; i < slots.length; i++) {
            int offset = i + 1; /* +1 to account for rumble ID */
            boolean rumbling = slots[i].isRumbling();
            byte state = (rumbling ? RUMBLE_START : RUMBLE_STOP);
            if (rumblePacket.get(offset) != state) {
                rumblePacket.put(offset, state);
                submit = true;
            }
        }

        /*
         * Only write to the adapter once the rumble packet has
         * been modified. It would be horrendous for performance
         * to submit these every update call.
         */
        if (submit) {
            Transfer transfer = LibUsb.allocTransfer();
            LibUsb.fillInterruptTransfer(transfer, usbHandle, ENDPOINT_OUT,
                    rumblePacket, this, null, 0);
            int result = LibUsb.submitTransfer(transfer);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException(result);
            }
        }
    }

    private void handleDataPacket(ByteBuffer packet) {
        for (GcWiiUSlotState slot : slots) {
            for (int i = 0; i < slot.data.length; i++) {
                slot.data[i] = packet.get();
            }
        }
    }

    @Override
    public void processTransfer(Transfer transfer) {
        if (closed) {
            return; /* received after adapter was closed */
        } else if (!usbHandle.equals(transfer.devHandle())) {
            return; /* not our device, not for us */
        }

        if (transfer.endpoint() == ENDPOINT_IN) {
            ByteBuffer buffer = transfer.buffer();
            byte packetId = buffer.get();
            if (packetId == DATA_ID) {
                this.handleDataPacket(buffer);
                this.requestedData = false;
            }
        }

        LibUsb.freeTransfer(transfer);
    }

    /**
     * TODO: docs
     */
    public synchronized void poll() {
        if (closed) {
            return;
        }

        if (!initializedAdapter) {
            this.initAdapter();
            this.initializedAdapter = true;
        }

        /*
         * This adapter utilizes asynchronous IO. As a result, it
         * must ask LibUsb to handle the events manually. If this
         * is not done, no data will come in from the transfers!
         */
        int result = LibUsb.handleEventsTimeout(usbContext, 0);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }

        /*
         * The requestedData boolean is used to prevent needless
         * transfers through the USB pipe. It is set to true when
         * data has been requested. When input data has arrived,
         * the handler will set requestedData to false again.
         */
        if (!requestedData) {
            Transfer transfer = LibUsb.allocTransfer();
            ByteBuffer data = ByteBuffer.allocateDirect(DATA_LEN);
            LibUsb.fillInterruptTransfer(transfer, usbHandle, ENDPOINT_IN,
                    data, this, null, 0L);

            result = LibUsb.submitTransfer(transfer);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException(result);
            }
            this.requestedData = true;
        }

        this.sendRumblePacket();
    }

    @Override
    public synchronized void close() {
        if (!closed) {
            for (GcWiiUSlotState slot : slots) {
                slot.clear();
            }
            try {
                this.sendRumblePacket();
            } catch (Exception e) {
                /* not necessary, just ignore */
            }
            this.closed = true;
        }
    }

}
