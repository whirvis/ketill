package io.ketill.hidusb.gc;

import io.ketill.AdapterSupplier;
import io.ketill.gc.GcController;
import org.jetbrains.annotations.NotNull;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Implementation for Nintendo's official Wii U GameCube controller adapter.
 * The adapter for individual controllers is {@link LibUsbGcAdapter}.
 * <p>
 * <b>Note:</b> For data to stay up-to-date, the adapter must be polled
 * periodically via the {@link #poll()} method. It is recommended to poll
 * the adapter once every application update.
 *
 * @see #getSlotSupplier(int)
 */
public final class GcWiiUAdapter implements Closeable {

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

    private final LibUsbDeviceGc usbDevice;
    private final GcWiiUSlotState[] slots;
    private final ByteBuffer rumblePacket;

    private boolean initializedAdapter;
    private boolean requestedData;
    private boolean closed;

    /**
     * @throws NullPointerException if {@code usbDevice} is {@code null}.
     */
    public GcWiiUAdapter(@NotNull LibUsbDeviceGc usbDevice) {
        this.usbDevice = Objects.requireNonNull(usbDevice,
                "usbDevice cannot be null");

        this.slots = new GcWiiUSlotState[SLOT_COUNT];
        for (int i = 0; i < slots.length; i++) {
            this.slots[i] = new GcWiiUSlotState();
        }

        /*
         * The size of this packet is equal to one (the rumble packet
         * ID) plus the amount of slots present on this adapter. Each
         * slot also takes up one byte for its rumble status.
         */
        this.rumblePacket = ByteBuffer.allocateDirect(1 + slots.length);
        rumblePacket.put(RUMBLE_ID);
    }

    private void requirePort(int port) {
        if (port < 0 || port >= slots.length) {
            throw new IndexOutOfBoundsException("no such port " + port);
        }
    }

    private void requireOpen() {
        if (closed) {
            throw new IllegalStateException("adapter closed");
        }
    }

    /**
     * @param port the port of the slot to check.
     * @return {@code true} if a GameCube controller is connected to the
     * specified port, {@code} false otherwise.
     * @throws IndexOutOfBoundsException if {@code port} is less than
     *                                   zero or greater than or equal
     *                                   to {@value #SLOT_COUNT}.
     * @throws IllegalStateException     if this adapter has been closed
     *                                   via {@link #close()}.
     * @see #getSlotSupplier(int)
     */
    public boolean isSlotConnected(int port) {
        this.requirePort(port);
        this.requireOpen();
        return slots[port].isConnected();
    }

    /**
     * @param port the port of the slot.
     * @return the adapter supplier for a GameCube controller connected to
     * the specified port. The same instance is returned on each invocation.
     * @throws IndexOutOfBoundsException if {@code port} is less than
     *                                   zero or greater than or equal
     *                                   to {@value #SLOT_COUNT}.
     * @throws IllegalStateException     if this adapter has been closed
     *                                   via {@link #close()}.
     * @see #isSlotConnected(int)
     */
    public @NotNull AdapterSupplier<GcController> getSlotSupplier(int port) {
        this.requirePort(port);
        this.requireOpen();
        return slots[port].supplier;
    }

    private void initAdapter() {
        /*
         * Before communication can occur with a Nintendo Wii U GameCube
         * controller adapter, the configuration interface must be claimed.
         * After this, the initialization packet can be sent.
         */
        usbDevice.claimInterface(CONFIG);

        /*
         * The initialization packet must be sent as an interrupt transfer
         * to the adapter. Once this is done, the adapter communication is
         * operational. Slot data can be read, rumble can be written, etc.
         */
        ByteBuffer init = ByteBuffer.allocateDirect(1);
        init.put(INIT_ID);
        usbDevice.interruptTransfer(ENDPOINT_OUT, init, 0L);
    }

    private void sendRumblePacket() {
        boolean submitRumble = false;

        /*
         * Prepare the rumble packet before sending it out. This
         * has the  added benefit of determining if any data needs
         * to be sent.
         */
        for (int i = 0; i < slots.length; i++) {
            int offset = i + 1; /* +1 to account for rumble ID */
            boolean rumbling = slots[i].isRumbling();
            byte state = (rumbling ? RUMBLE_START : RUMBLE_STOP);
            if (rumblePacket.get(offset) != state) {
                rumblePacket.put(offset, state);
                submitRumble = true;
            }
        }

        /*
         * Only send an interrupt transfer to the adapter if the rumble
         * packet has been modified. It would be horrendous performance
         * wise to submit them every update call.
         *
         * Furthermore, the status of the transfer is not checked. If an
         * error occurs, broken rumble does not deem an exception. It is
         * more likely that the USB device was unplugged while the packet
         * was being generated.
         */
        if (submitRumble) {
            try {
                Transfer transfer = LibUsb.allocTransfer();
                usbDevice.fillInterruptTransfer(transfer, ENDPOINT_OUT,
                        rumblePacket, null, null, 0L);
                LibUsb.submitTransfer(transfer);
            } catch (LibUsbException e) {
                /* expected possibility, ignore */
            }
        }
    }

    private void handleDataPacket(ByteBuffer packet) {
        synchronized (slots) {
            for (GcWiiUSlotState slot : slots) {
                for (int i = 0; i < slot.data.length; i++) {
                    slot.data[i] = packet.get();
                }
            }
        }
    }

    private void processTransfer(ByteBuffer buffer) {
        byte packetId = buffer.get();
        if (packetId == DATA_ID) {
            this.handleDataPacket(buffer);
            this.requestedData = false;
        }
    }

    /**
     * Performs a <i>single</i> query from the Wii U adapter and updates the
     * rumble state of all GameCube controllers. It is recommended to call
     * this method once every application update.
     *
     * @see #isSlotConnected(int)
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
        usbDevice.handleEventsCompleted(0L);

        /*
         * The requestedData boolean is used to prevent needless
         * transfers through the USB pipe. It is set to true when
         * data has been requested. When input data has arrived,
         * the handler will set requestedData to false again.
         */
        if (!requestedData) {
            Transfer transfer = LibUsb.allocTransfer();
            ByteBuffer data = ByteBuffer.allocateDirect(DATA_LEN);
            usbDevice.fillInterruptTransfer(transfer, ENDPOINT_IN, data,
                    this::processTransfer, null, 0L);
            usbDevice.submitTransfer(transfer);
            this.requestedData = true;
        }

        /*
         * After requesting data, update the slot state with the
         * current received data. Put this in a synchronized block
         * just in case data comes in while these controllers are
         * being updated when new data comes in.
         */
        synchronized (slots) {
            for (GcWiiUSlotState slot : slots) {
                slot.poll();
            }
        }

        this.sendRumblePacket();
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }

        /*
         * Since the adapter is closed, clear the slot data of every
         * controller. This ensures the next time they are polled
         * they display a default state (e.g., no buttons pressed).
         */
        for (GcWiiUSlotState slot : slots) {
            slot.reset();
        }

        /*
         * In case any controllers are rumbling at the time this adapter
         * is closed, send one last rumble packet. Now that the slots are
         * reset, they will all have rumble disabled. This will prevent
         * the controllers from continually rumbling after this closes.
         */
        this.sendRumblePacket();

        this.closed = true;
    }

}
