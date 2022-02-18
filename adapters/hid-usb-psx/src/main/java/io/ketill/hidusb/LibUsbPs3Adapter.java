package io.ketill.hidusb;

import io.ketill.FeatureAdapter;
import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.Button1b;
import io.ketill.controller.DeviceButton;
import io.ketill.controller.Trigger1f;
import io.ketill.controller.Vibration1f;
import io.ketill.psx.Ps3Controller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usb4java.Context;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;
import org.usb4java.TransferCallback;

import java.nio.ByteBuffer;
import java.util.Objects;

import static io.ketill.psx.Ps3Controller.*;

public final class LibUsbPs3Adapter extends IoDeviceAdapter<Ps3Controller> implements TransferCallback {

    /* @formatter:off */
    private static final byte
            CONFIG       = (byte) 0x00,
            ENDPOINT_IN  = (byte) 0x81,
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

    private static final int INPUT_SIZE = 64;

    private static ByteBuffer wrapDirectBuffer(byte[] data) {
        ByteBuffer wrapped = ByteBuffer.allocateDirect(data.length);
        wrapped.put(data);
        return wrapped;
    }

    private final Context usbContext;
    private final DeviceHandle usbHandle;
    private final ByteBuffer hidReport;
    private ByteBuffer input;

    private boolean initialized;
    private boolean connected;
    private boolean requestedData;
    private byte rumbleWeak;
    private byte rumbleStrong;

    /**
     * @param controller the controller which owns this adapter.
     * @param registry   the controller's mapped feature registry.
     * @param usbContext the LibUSB context, may be {@code null}.
     * @param usbHandle  the USB handle.
     * @throws NullPointerException if {@code controller}, {@code registry},
     *                              or {@code usbHandle} are {@code} null.
     */
    public LibUsbPs3Adapter(@NotNull Ps3Controller controller,
                            @NotNull MappedFeatureRegistry registry,
                            @Nullable Context usbContext,
                            @NotNull DeviceHandle usbHandle) {
        super(controller, registry);
        this.usbContext = usbContext;
        this.usbHandle = Objects.requireNonNull(usbHandle, "usbHandle");
        this.hidReport = wrapDirectBuffer(HID_REPORT);
        this.input = ByteBuffer.allocateDirect(INPUT_SIZE);
    }

    @MappingMethod
    private void mapButton(@NotNull DeviceButton button, int byteOffset,
                           int bitIndex) {
        registry.mapFeature(button, new ButtonMapping(byteOffset, bitIndex),
                this::updateButton);
    }

    @MappingMethod
    private void mapTrigger(@NotNull AnalogTrigger trigger, int byteOffset) {
        registry.mapFeature(trigger, byteOffset, this::updateForce);
    }

    @Override
    protected void initAdapter() {
        this.mapButton(BUTTON_SELECT, 2, 0);
        this.mapButton(BUTTON_L_THUMB, 2, 1);
        this.mapButton(BUTTON_R_THUMB, 2, 2);
        this.mapButton(BUTTON_START, 2, 3);
        this.mapButton(BUTTON_UP, 2, 4);
        this.mapButton(BUTTON_DOWN, 2, 6);
        this.mapButton(BUTTON_RIGHT, 2, 5);
        this.mapButton(BUTTON_LEFT, 2, 7);
        this.mapButton(BUTTON_L2, 3, 0);
        this.mapButton(BUTTON_R2, 3, 1);
        this.mapButton(BUTTON_L1, 3, 2);
        this.mapButton(BUTTON_R1, 3, 3);
        this.mapButton(BUTTON_SQUARE, 3, 7);
        this.mapButton(BUTTON_TRIANGLE, 3, 4);
        this.mapButton(BUTTON_CIRCLE, 3, 5);
        this.mapButton(BUTTON_CROSS, 3, 6);

        /* TODO: analog sticks */

        this.mapTrigger(TRIGGER_LT, 18);
        this.mapTrigger(TRIGGER_RT, 19);

        registry.mapFeature(MOTOR_WEAK, 2, this::updateWeakMotor);
        registry.mapFeature(MOTOR_STRONG, 4, this::updateStrongMotor);
    }

    @FeatureAdapter
    private void updateButton(Button1b button, ButtonMapping mapping) {
        int bits = input.get(mapping.byteOffset) & 0xFF;
        button.pressed = (bits & (1 << mapping.bitIndex)) != 0;
    }

    @FeatureAdapter
    private void updateForce(Trigger1f trigger, int byteOffset) {
        int value = input.get(byteOffset) & 0xFF;
        trigger.force = (value / 255.0F);
    }

    @FeatureAdapter
    private void updateWeakMotor(@NotNull Vibration1f vibration,
                                 int byteOffset) {
        byte rumbleWeak = (byte) (vibration.force > 0.0F ? 0x01 : 0x00);
        if (rumbleWeak != this.rumbleWeak) {
            hidReport.put(byteOffset, rumbleWeak);
            this.rumbleWeak = rumbleWeak;
            this.sendReport();
        }
    }

    @FeatureAdapter
    private void updateStrongMotor(@NotNull Vibration1f vibration,
                                   int byteOffset) {
        byte rumbleStrong = (byte) (vibration.force * 255.0F);
        if (rumbleStrong != this.rumbleStrong) {
            hidReport.put(byteOffset, rumbleStrong);
            this.rumbleStrong = rumbleStrong;
            this.sendReport();
        }
    }

    /* TODO: LEDs */

    @Override
    public void processTransfer(Transfer transfer) {
        if (!usbHandle.equals(transfer.devHandle())) {
            return; /* not our device */
        } else if (transfer.endpoint() == ENDPOINT_IN) {
            this.input = transfer.buffer();
            this.requestedData = false;
        }
        LibUsb.freeTransfer(transfer);
    }

    private void initDevice() {
        if (initialized) {
            return;
        }

        int result = LibUsb.claimInterface(usbHandle, CONFIG);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException(result);
        }

        ByteBuffer setup = wrapDirectBuffer(SETUP);
        int transferred = LibUsb.controlTransfer(usbHandle, REQUEST_TYPE,
                REQUEST, SETUP_VALUE, INDEX, setup, 0L);
        if (transferred < 0) {
            throw new LibUsbException(transferred);
        }

        this.initialized = true;
        this.connected = true;
    }

    private void sendReport() {
        int result = LibUsb.controlTransfer(usbHandle, REQUEST_TYPE, REQUEST,
                REPORT_VALUE, INDEX, hidReport, 0L);
        if (result < 0) {
            throw new LibUsbException(result);
        }
    }

    @Override
    protected void pollDevice() {
        if (!initialized) {
            this.initDevice();
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
            ByteBuffer input = ByteBuffer.allocateDirect(INPUT_SIZE);
            LibUsb.fillInterruptTransfer(transfer, usbHandle, ENDPOINT_IN,
                    input, this, null, 0);

            result = LibUsb.submitTransfer(transfer);
            if (result == LibUsb.ERROR_IO) {
                this.connected = false;
            } else if (result != LibUsb.SUCCESS) {
                throw new LibUsbException(result);
            }

            this.requestedData = true;
        }
    }

    @Override
    protected boolean isDeviceConnected() {
        return this.connected;
    }

}
