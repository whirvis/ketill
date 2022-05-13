package io.ketill.hidusb.psx;

import io.ketill.FeatureAdapter;
import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.ButtonStateZ;
import io.ketill.controller.DeviceButton;
import io.ketill.controller.LedState;
import io.ketill.controller.MotorVibration;
import io.ketill.controller.StickPosZ;
import io.ketill.controller.TriggerStateZ;
import io.ketill.psx.Ps3Controller;
import org.jetbrains.annotations.NotNull;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;

import java.nio.ByteBuffer;
import java.util.Objects;

import static io.ketill.psx.Ps3Controller.*;

public final class LibUsbPs3Adapter extends IoDeviceAdapter<Ps3Controller> {

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

    private static final byte[] LED_PATTERNS = new byte[]{
            0b0000,                         /* disable LEDs      */
            0b0001, 0b0010, 0b0100, 0b1000, /* players #1 to #4  */
            0b1001, 0b1010, 0b1100, 0b1101, /* players #5 to #8  */
            0b1110, 0b1111                  /* players #9 to #10 */
    };
    /* @formatter:on */

    private static final int INPUT_SIZE = 64;

    private static ByteBuffer wrapDirectBuffer(byte[] data) {
        ByteBuffer wrapped = ByteBuffer.allocateDirect(data.length);
        wrapped.put(data);
        return wrapped;
    }

    private final LibUsbDevicePs3 usbDevice;
    private final ByteBuffer hidReport;
    private ByteBuffer input;

    private boolean initialized;
    private boolean connected;
    private boolean requestedData;
    private byte rumbleWeak;
    private byte rumbleStrong;
    private byte ledByte;

    /**
     * @param controller the controller which owns this device.
     * @param registry   the controller's mapped feature registry.
     * @param usbDevice  the USB device.
     * @throws NullPointerException if {@code controller}, {@code registry},
     *                              or {@code usbDevice} are {@code} null.
     */
    public LibUsbPs3Adapter(@NotNull Ps3Controller controller,
                            @NotNull MappedFeatureRegistry registry,
                            @NotNull LibUsbDevicePs3 usbDevice) {
        super(controller, registry);
        this.usbDevice = Objects.requireNonNull(usbDevice,
                "usbDevice cannot be null");
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
    @SuppressWarnings("SameParameterValue")
    void mapStick(@NotNull AnalogStick stick, int byteOffsetX,
                  int byteOffsetY, int thumbByteOffset, int thumbBitIndex) {
        registry.mapFeature(stick, new StickMapping(byteOffsetX, byteOffsetY,
                thumbByteOffset, thumbBitIndex), this::updateStick);
    }

    @MappingMethod
    private void mapTrigger(@NotNull AnalogTrigger trigger, int byteOffset) {
        registry.mapFeature(trigger, byteOffset, this::updateTrigger);
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

        this.mapStick(STICK_LS, 6, 7, 2, 1);
        this.mapStick(STICK_RS, 8, 9, 2, 2);

        this.mapTrigger(TRIGGER_LT, 18);
        this.mapTrigger(TRIGGER_RT, 19);

        registry.mapFeature(MOTOR_WEAK, 2, this::updateWeakMotor);
        registry.mapFeature(MOTOR_STRONG, 4, this::updateStrongMotor);

        registry.mapFeature(FEATURE_LED, 9, this::updateLed);
    }

    private boolean isPressed(int byteOffset, int bitIndex) {
        int bits = input.get(byteOffset) & 0xFF;
        return (bits & (1 << bitIndex)) != 0;
    }

    @FeatureAdapter
    private void updateButton(@NotNull ButtonStateZ button,
                              @NotNull ButtonMapping mapping) {
        button.pressed = this.isPressed(mapping.byteOffset, mapping.bitIndex);
    }

    @FeatureAdapter
    private void updateStick(@NotNull StickPosZ state,
                             @NotNull StickMapping mapping) {
        int posX = input.get(mapping.byteOffsetX) & 0xFF;
        int posY = input.get(mapping.byteOffsetY) & 0xFF;

        boolean pressed = false;
        if (mapping.hasThumb) {
            pressed = this.isPressed(mapping.thumbByteOffset,
                    mapping.thumbBitIndex);
        }

        /*
         * This may look confusing, but it's just some normalization. The
         * analog sticks are first converted from a 0x00 to 0xFF scale to
         * a 0.0F to 1.0F scale. However, this is not sufficient for the
         * input API; it uses a scale of -1.0F to 1.0F for analog sticks.
         *
         * The X-axis starts from the left at (at 0.0F) and ends at the
         * very right (1.0F.). The Y-axis starts at the very at the very
         * top (0.0F) and ends at the very bottom (1.0F.)
         */
        state.pos.x = ((posX / 255.0F) * 2.0F) - 1.0F;
        state.pos.y = ((posY / 255.0F) * -2.0F) + 1.0F;
        state.pos.z = pressed ? -1.0F : 0.0F;
    }

    @FeatureAdapter
    private void updateTrigger(@NotNull TriggerStateZ state, int byteOffset) {
        int value = input.get(byteOffset) & 0xFF;
        state.force = (value / 255.0F);
    }

    @FeatureAdapter
    private void updateWeakMotor(@NotNull MotorVibration vibration,
                                 int byteOffset) {
        byte rumbleWeak = (byte) (vibration.getStrength() > 0.0F ? 0x01 : 0x00);
        if (rumbleWeak != this.rumbleWeak) {
            hidReport.put(byteOffset, rumbleWeak);
            this.rumbleWeak = rumbleWeak;
            this.sendReport();
        }
    }

    @FeatureAdapter
    private void updateStrongMotor(@NotNull MotorVibration vibration,
                                   int byteOffset) {
        byte rumbleStrong = (byte) (vibration.getStrength() * 255.0F);
        if (rumbleStrong != this.rumbleStrong) {
            hidReport.put(byteOffset, rumbleStrong);
            this.rumbleStrong = rumbleStrong;
            this.sendReport();
        }
    }

    @FeatureAdapter
    private void updateLed(@NotNull LedState led, int byteOffset) {
        byte ledByte = 0x00; /* default to off */

        int mode = led.getMode();
        if (mode == LedState.MODE_NUMBER) {
            int index = led.getValue();
            if (index >= 0 && index < LED_PATTERNS.length) {
                ledByte = LED_PATTERNS[index];
            }
        } else if (mode == LedState.MODE_PATTERN) {
            ledByte = (byte) led.getValue();
        }

        /*
         * The LEDs at this byte start at bit index one, rather
         * than zero. To accommodate for this, shift the value
         * of ledByte to the left by a single bit.
         */
        ledByte = (byte) (ledByte << 1);

        if (ledByte != this.ledByte) {
            hidReport.put(byteOffset, ledByte);
            this.ledByte = ledByte;
            this.sendReport();
        }
    }

    private void initDevice() {
        if (initialized) {
            return;
        }

        usbDevice.claimInterface(CONFIG);

        ByteBuffer setup = wrapDirectBuffer(SETUP);
        usbDevice.controlTransfer(REQUEST_TYPE, REQUEST, SETUP_VALUE, INDEX,
                setup, 0L);

        this.initialized = true;
        this.connected = true;
    }

    private void sendReport() {
        usbDevice.controlTransfer(REQUEST_TYPE, REQUEST, REPORT_VALUE,
                INDEX, hidReport, 0L);
    }

    private void handleTransfer(ByteBuffer buffer) {
        this.input = buffer;
        this.requestedData = false;
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
        usbDevice.handleEventsTimeout(0L);

        /*
         * The requestedData boolean is used to prevent needless
         * transfers through the USB pipe. It is set to true when
         * data has been requested. When input data has arrived,
         * the handler will set requestedData to false again.
         */
        if (!requestedData) {
            Transfer transfer = LibUsb.allocTransfer();
            ByteBuffer input = ByteBuffer.allocateDirect(INPUT_SIZE);
            usbDevice.fillInterruptTransfer(transfer, ENDPOINT_IN, input,
                    this::handleTransfer, null, 0L);

            int result = LibUsb.submitTransfer(transfer);
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
