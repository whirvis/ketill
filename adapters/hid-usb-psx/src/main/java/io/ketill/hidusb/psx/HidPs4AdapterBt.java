package io.ketill.hidusb.psx;

import io.ketill.MappedFeatureRegistry;
import io.ketill.psx.Ps4Controller;
import org.hid4java.HidDevice;
import org.jetbrains.annotations.NotNull;

import static io.ketill.psx.Ps4Controller.*;

public final class HidPs4AdapterBt extends HidPs4Adapter {

    private static final byte INPUT_ID = (byte) 0x11;
    private static final byte OUTPUT_ID = (byte) 0x15;

    private static final int INPUT_LEN = 78;
    private static final int OUTPUT_LEN = 329;

    /*
     * When connected via Bluetooth, PS4 controllers will sometimes
     * send input reports on channel 0x01. This adapter is programmed
     * to handle input reports on channel 0x11. Sending the message
     * below will signal the PS4 controller to switch its reporting
     * mode from channel 0x01 to channel 0x11.
     */
    /* @formatter:off */
    private static final byte ACTIVATE_11_REPORT_ID = (byte) 0x02;
    private static final byte[] ACTIVATE_11_MSG = {
            (byte) 0x01, (byte) 0x00, (byte) 0xff, (byte) 0xff,
            (byte) 0x01, (byte) 0x00, (byte) 0x5e, (byte) 0x22,
            (byte) 0x84, (byte) 0x22, (byte) 0x9b, (byte) 0x22,
            (byte) 0xa6, (byte) 0xdd, (byte) 0x79, (byte) 0xdd,
            (byte) 0x64, (byte) 0xdd, (byte) 0x1c, (byte) 0x02,
            (byte) 0x1c, (byte) 0x02, (byte) 0x85, (byte) 0x1f,
            (byte) 0x9f, (byte) 0xe0, (byte) 0x92, (byte) 0x20,
            (byte) 0xdc, (byte) 0xe0, (byte) 0x4d, (byte) 0x1c,
            (byte) 0x1e, (byte) 0xde, (byte) 0x08, (byte) 0x00
    };
    /* @formatter:on */

    /**
     * @param controller the controller which owns this adapter.
     * @param registry   the controller's mapped feature registry.
     * @param hidDevice  the HID device, must be open.
     * @throws NullPointerException  if {@code controller}, {@code registry},
     *                               or {@code hidDevice} are {@code} null.
     * @throws IllegalStateException if {@code hidDevice} is not open.
     */
    public HidPs4AdapterBt(@NotNull Ps4Controller controller,
                           @NotNull MappedFeatureRegistry registry,
                           @NotNull HidDevice hidDevice) {
        super(controller, registry, hidDevice, INPUT_ID, OUTPUT_ID);
        hidDevice.getFeatureReport(ACTIVATE_11_MSG, ACTIVATE_11_REPORT_ID);
    }

    @Override
    protected byte[] generateInputReport() {
        byte[] report = new byte[INPUT_LEN];
        report[0] = INPUT_ID;
        report[1] = (byte) 0xC0; /* poll rate */
        report[2] = (byte) 0x00; /* unknown */
        populateInputReport(report, 3);
        return report;
    }

    @Override
    protected byte[] generateOutputReport() {
        byte[] report = new byte[OUTPUT_LEN];
        report[0] = (byte) 0xC0; /* poll rate */
        report[1] = (byte) 0xA0; /* unknown */
        report[2] = (byte) 0xF7; /* enable all features */
        report[3] = (byte) 0x04; /* unknown */
        return report;
    }

    @Override
    protected byte[] getChecksumHeader() {
        byte[] header = new byte[1];
        header[0] = (byte) 0xA2; /* data output */
        return header;
    }

    @Override
    protected void initAdapter() {
        this.mapDpad(BUTTON_UP, 7, DPAD_PATTERNS_UP);
        this.mapDpad(BUTTON_DOWN, 7, DPAD_PATTERNS_DOWN);
        this.mapDpad(BUTTON_LEFT, 7, DPAD_PATTERNS_LEFT);
        this.mapDpad(BUTTON_RIGHT, 7, DPAD_PATTERNS_RIGHT);

        this.mapButton(BUTTON_SQUARE, 7, 4);
        this.mapButton(BUTTON_CROSS, 7, 5);
        this.mapButton(BUTTON_CIRCLE, 7, 6);
        this.mapButton(BUTTON_TRIANGLE, 7, 7);
        this.mapButton(BUTTON_L1, 8, 0);
        this.mapButton(BUTTON_R1, 8, 1);
        this.mapButton(BUTTON_L2, 8, 2);
        this.mapButton(BUTTON_R2, 8, 3);
        this.mapButton(BUTTON_SHARE, 8, 4);
        this.mapButton(BUTTON_OPTIONS, 8, 5);
        this.mapButton(BUTTON_L_THUMB, 8, 6);
        this.mapButton(BUTTON_R_THUMB, 8, 7);
        this.mapButton(BUTTON_PS, 9, 0);
        this.mapButton(BUTTON_TPAD, 9, 1);

        this.mapStick(STICK_LS, 3, 4, 8, 6);
        this.mapStick(STICK_LS, 5, 6, 8, 7);

        this.mapTrigger(TRIGGER_LT, 10);
        this.mapTrigger(TRIGGER_RT, 11);

        this.mapMotor(MOTOR_WEAK, 5);
        this.mapMotor(MOTOR_STRONG, 6);

        this.mapLightbar(7);
    }

}
