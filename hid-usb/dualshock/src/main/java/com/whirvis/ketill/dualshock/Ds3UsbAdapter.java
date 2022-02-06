package com.whirvis.ketill.dualshock;

import com.whirvis.ketill.AnalogTrigger;
import com.whirvis.ketill.Button1b;
import com.whirvis.ketill.IoDeviceAdapter;
import com.whirvis.ketill.DeviceButton;
import com.whirvis.ketill.FeatureAdapter;
import com.whirvis.ketill.MappedFeatureRegistry;
import com.whirvis.ketill.Trigger1f;
import com.whirvis.ketill.Vibration1f;
import com.whirvis.ketill.psx.Ps3Controller;
import org.jetbrains.annotations.NotNull;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;
import org.usb4java.TransferCallback;

import java.nio.ByteBuffer;
import java.util.Objects;

import static com.whirvis.ketill.psx.Ps3Controller.*;

/**
 * An adapter which maps input for a DualShock 3 USB input device.
 */
public class Ds3UsbAdapter extends IoDeviceAdapter<Ps3Controller> implements TransferCallback {

    /* @formatter:off */
    private static final byte
                CONFIG       = (byte) 0x00,
                ENDPOINT_IN  = (byte) 0x81;

    private static final byte
                REQUEST_TYPE = (byte) 0x21,
                REQUEST      = (byte) 0x09,
                INDEX        = (byte) 0x00;

    private static final short
                SETUP_VALUE = (short) 0x03F4,
                REPORT_VALUE = (short) 0x0201;

    private static final byte[] SETUP = {
            0x42, 0x0C, 0x00, 0x00
    };

    private static final byte[] HID_REPORT = {
            (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0xFF, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0xFF, (byte) 0x27, (byte) 0x10, (byte) 0x00, (byte) 0x32,
            (byte) 0xFF, (byte) 0x27, (byte) 0x10, (byte) 0x00, (byte) 0x32,
            (byte) 0xFF, (byte) 0x27, (byte) 0x10, (byte) 0x00, (byte) 0x32,
            (byte) 0xFF, (byte) 0x27, (byte) 0x10, (byte) 0x00, (byte) 0x32,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    /* @formatter:on */

    private static final byte WEAK_MOTOR_OFFSET = 2;
    private static final byte STRONG_MOTOR_OFFSET = 4;

    private static ByteBuffer wrapDirectBuffer(byte[] data) {
        ByteBuffer buf = ByteBuffer.allocateDirect(data.length);
        buf.put(data);
        return buf;
    }

    private final DeviceHandle handle;
    private final ByteBuffer hidReport;
    private ByteBuffer input;

    private boolean initialized;
    private boolean connected;
    private boolean requestedData;
    private byte rumbleWeak;
    private byte rumbleStrong;

    public Ds3UsbAdapter(DeviceHandle handle) {
        this.handle = Objects.requireNonNull(handle, "handle");
        this.hidReport = wrapDirectBuffer(HID_REPORT);
        this.input = ByteBuffer.allocateDirect(64);
    }

    private void mapButton(@NotNull MappedFeatureRegistry registry,
                           @NotNull DeviceButton button, int byteOffset,
                           int bitIndex) {
        registry.mapFeature(button, new Ds3ButtonMapping(byteOffset,
                bitIndex), this::updateButton);
    }

    private void mapTrigger(@NotNull MappedFeatureRegistry registry,
                            @NotNull AnalogTrigger trigger, int byteOffset) {
        registry.mapFeature(trigger, byteOffset, this::updateForce);
    }

    @Override
    protected void initAdapter(@NotNull Ps3Controller device,
                               @NotNull MappedFeatureRegistry registry) {
        this.mapButton(registry, BUTTON_SELECT, 2, 0);
        this.mapButton(registry, BUTTON_L_THUMB, 2, 1);
        this.mapButton(registry, BUTTON_R_THUMB, 2, 2);
        this.mapButton(registry, BUTTON_START, 2, 3);
        this.mapButton(registry, BUTTON_UP, 2, 4);
        this.mapButton(registry, BUTTON_DOWN, 2, 6);
        this.mapButton(registry, BUTTON_RIGHT, 2, 5);
        this.mapButton(registry, BUTTON_LEFT, 2, 7);
        this.mapButton(registry, BUTTON_L2, 3, 0);
        this.mapButton(registry, BUTTON_R2, 3, 1);
        this.mapButton(registry, BUTTON_L1, 3, 2);
        this.mapButton(registry, BUTTON_R1, 3, 3);
        this.mapButton(registry, BUTTON_SQUARE, 3, 7);
        this.mapButton(registry, BUTTON_TRIANGLE, 3, 4);
        this.mapButton(registry, BUTTON_CIRCLE, 3, 5);
        this.mapButton(registry, BUTTON_CROSS, 3, 6);

        /* TODO: analog sticks */

        this.mapTrigger(registry, TRIGGER_LT, 18);
        this.mapTrigger(registry, TRIGGER_RT, 19);

        registry.mapFeature(MOTOR_WEAK, this::doWeakRumble);
        registry.mapFeature(MOTOR_STRONG, this::doStrongRumble);
    }

    @FeatureAdapter
    public void updateButton(Button1b button, Ds3ButtonMapping mapping) {
        int bits = input.get(mapping.byteOffset) & 0xFF;
        button.pressed = (bits & (1 << mapping.bitIndex)) != 0;
    }

    @FeatureAdapter
    public void updateForce(Trigger1f trigger, int byteOffset) {
        int value = input.get(byteOffset) & 0xFF;
        trigger.force = (value / 255.0F);
    }

    @FeatureAdapter
    public void doWeakRumble(Vibration1f vibration) {
        byte rumbleWeak = (byte) (vibration.force > 0.0F ? 0x01 : 0x00);
        if (rumbleWeak != this.rumbleWeak) {
            hidReport.put(WEAK_MOTOR_OFFSET, rumbleWeak);
            this.rumbleWeak = rumbleWeak;
            this.sendReport();
        }
    }

    @FeatureAdapter
    public void doStrongRumble(Vibration1f vibration) {
        byte rumbleStrong = (byte) (vibration.force * 255.0F);
        if (rumbleStrong != this.rumbleStrong) {
            hidReport.put(STRONG_MOTOR_OFFSET, rumbleStrong);
            this.rumbleStrong = rumbleStrong;
            this.sendReport();
        }
    }


    /* TODO: LEDs */

    @Override
    public boolean isDeviceConnected(@NotNull Ps3Controller controller) {
        return this.connected;
    }

    @Override
    public void processTransfer(Transfer transfer) {
        if (!handle.equals(transfer.devHandle())) {
            return; /* not our device */
        } else if (transfer.endpoint() == ENDPOINT_IN) {
            this.input = transfer.buffer();
            this.requestedData = false;
        }
        LibUsb.freeTransfer(transfer);
    }

    private void initDevice() {
        int result = LibUsb.claimInterface(handle, CONFIG);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }

        ByteBuffer setup = wrapDirectBuffer(SETUP);
        int transferred = LibUsb.controlTransfer(handle, REQUEST_TYPE,
                REQUEST, SETUP_VALUE, INDEX, setup, 0L);
        if (transferred < 0) {
            throw new LibUsbException(transferred);
        }

        this.initialized = true;
        this.connected = true;
    }

    private void sendReport() {
        int result = LibUsb.controlTransfer(handle, REQUEST_TYPE, REQUEST,
                REPORT_VALUE, INDEX, hidReport, 0L);
        if (result < 0) {
            throw new LibUsbException(result);
        }
    }

    @Override
    public void pollDevice(@NotNull Ps3Controller c) {
        if (!initialized) {
            this.initDevice();
        } else if (!this.isDeviceConnected(c)) {
            return; /* no device present */
        }

        /*
         * The USB device code makes use of asynchronous IO. As such, it must
         * ask LibUsb to handle the events manually. If this is not done, no
         * data will come in for transfers!
         */
        int result = LibUsb.handleEventsTimeout(null, 0);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }

        /*
         * The requestedData boolean is used to prevent needless transfers to
         * through the USB pipe (in hopes of increasing performance.) It is set
         * to true when data has been requested. When input data has arrived,
         *  the
         * handler will set requestedData to false again.
         */
        if (!requestedData) {
            Transfer transfer = LibUsb.allocTransfer();
            ByteBuffer input = ByteBuffer.allocateDirect(64);
            LibUsb.fillInterruptTransfer(transfer, handle, ENDPOINT_IN, input
                    , this, null, 0);

            result = LibUsb.submitTransfer(transfer);
            if (result == LibUsb.ERROR_IO) {
                this.connected = false;
            } else if (result != LibUsb.SUCCESS) {
                throw new LibUsbException(result);
            }

            this.requestedData = true;
        }
    }

}
