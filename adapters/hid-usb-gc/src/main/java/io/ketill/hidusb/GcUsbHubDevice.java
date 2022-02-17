package io.ketill.hidusb;

import org.jetbrains.annotations.NotNull;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;
import org.usb4java.TransferCallback;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GcUsbHubDevice implements TransferCallback, Closeable {

    /* @formatter:off */
    public static final short
            VENDOR_ID        = 0x057E,
            PRODUCT_ID       = 0x0337;

    private static final byte
            CONFIG           = (byte) 0x00,
            ENDPOINT_IN      = (byte) 0x81,
            ENDPOINT_OUT     = (byte) 0x02;

    private static final byte
            RUMBLE_ID        = 0x11,
            INIT_ID          = 0x13,
            DATA_ID          = 0x21;

    private static final byte
            RUMBLE_STOP      = 0x00,
            RUMBLE_START     = 0x01;
         /* RUMBLE_STOP_HARD = 0x02; */

    private static final int
            DATA_LENGTH      = 37;
    /* @formatter:on */

    protected static boolean isAdapter(@NotNull Device device) {
        DeviceDescriptor desc = new DeviceDescriptor();
        LibUsb.getDeviceDescriptor(device, desc);
        return desc.idVendor() == VENDOR_ID && desc.idProduct() == PRODUCT_ID;
    }

    protected static boolean isAdapter(@NotNull DeviceHandle handle) {
        return isAdapter(LibUsb.getDevice(handle));
    }

    private final DeviceHandle handle;
    private boolean initialized;

    private boolean requestedSlots;
    private final byte[][] slots;
    private final ByteBuffer rumble;

    private final List<LibUsbGcAdapterSupplier> adapterSuppliers;

    protected GcUsbHubDevice(@NotNull DeviceHandle handle) {
        this.handle = handle;

        this.slots = new byte[4][9];
        this.rumble = ByteBuffer.allocateDirect(1 + slots.length);
        rumble.put(RUMBLE_ID);

        this.adapterSuppliers = new ArrayList<>();
        for(int i = 0; i < slots.length; i++) {
            adapterSuppliers.add(new LibUsbGcAdapterSupplier(this, i));
        }
    }

    public List<LibUsbGcAdapterSupplier> getAdapterSuppliers() {
        return Collections.unmodifiableList(adapterSuppliers);
    }

    protected byte[] getSlotData(int slot) {
        return this.slots[slot];
    }

    private void initAdapter() {
        if (initialized) {
            throw new IllegalStateException("already initialized");
        }

        int result = LibUsb.claimInterface(handle, CONFIG);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }

        ByteBuffer init = ByteBuffer.allocateDirect(1);
        init.put(INIT_ID);
        IntBuffer transferred = IntBuffer.allocate(1);
        result = LibUsb.interruptTransfer(handle, ENDPOINT_OUT, init,
                transferred, 0);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }

        this.initialized = true;
    }

    private void handlePacket(ByteBuffer packet) {
        byte id = packet.get();
        if (id != DATA_ID) {
            return;
        }
        for (byte[] slot : slots) {
            for (int j = 0; j < slot.length; j++) {
                slot[j] = packet.get();
            }
        }
        this.requestedSlots = false;
    }

    private void updateRumble() {
        boolean submit = false;

        for (int i = 0; i < slots.length; i++) {
            int offset = i + 1; /* account for rumble ID */
            boolean rumbling = false;//adapters[i].isRumbling();
            byte state = (rumbling ? RUMBLE_START : RUMBLE_STOP);
            if (rumble.get(offset) != state) {
                rumble.put(offset, state);
                submit = true;
            }
        }

        /*
         * Only write to the adapter once the rumble packet has been modified.
         * It would be horrendous for performance to send these signals every
         * update call.
         */
        if (submit) {
            Transfer transfer = LibUsb.allocTransfer();
            LibUsb.fillInterruptTransfer(transfer, handle, ENDPOINT_OUT,
                    rumble, this, null, 0);
            int result = LibUsb.submitTransfer(transfer);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException(result);
            }
        }
    }

    @Override
    public void processTransfer(Transfer transfer) {
        if (!handle.equals(transfer.devHandle())) {
            return; /* not our device */
        } else if (transfer.endpoint() == ENDPOINT_IN) {
            this.handlePacket(transfer.buffer());
        }
        LibUsb.freeTransfer(transfer);
    }

    public boolean isSlotConnected(int port) {
        /*
         * The first byte is the current controller type. This will be used to
         * determine if the controller is connected, and regardless of whether
         * it self-reports to be wireless. Wireless controllers are usually
         * Wavebird controllers. However, there is no guarantee for this.
         */
        int type = (slots[port][0] & 0xFF) >> 4;
        return type > 0;
    }

    public void poll() {
        if (!initialized) {
            this.initAdapter();
        }

        /*
         * The USB device code makes use of asynchronous IO. As such, it
         * must ask LibUsb to handle the events manually. If this is not
         * done, no data will come in for transfers!
         */
        int result = LibUsb.handleEventsTimeout(null, 0);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }

        /*
         * The requestedSlots boolean is used to prevent needless transfers
         * to through the USB pipe (in hopes of increasing performance.) It
         * is set to true when data has been requested. When slot data has
         * arrived, the handler will set requestedSlots to false again.
         */
        if (!requestedSlots) {
            Transfer transfer = LibUsb.allocTransfer();
            ByteBuffer data = ByteBuffer.allocateDirect(DATA_LENGTH);
            LibUsb.fillInterruptTransfer(transfer, handle, ENDPOINT_IN, data,
                    this, null, 0L);

            result = LibUsb.submitTransfer(transfer);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException(result);
            }
            this.requestedSlots = true;
        }

        this.updateRumble();
    }

    @Override
    public void close() {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }

        /* zero out slot data to prevent false positives */
        for (byte[] slot : slots) {
            Arrays.fill(slot, (byte) 0x00);
        }

        /* attempt stop rumbling if possible */
        try {
            this.updateRumble();
        } catch (Exception e) {
            /* oh well, we tried */
        }

        this.initialized = false;
    }

}
