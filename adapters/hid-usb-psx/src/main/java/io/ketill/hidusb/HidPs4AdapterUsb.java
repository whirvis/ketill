package io.ketill.hidusb;

import io.ketill.MappedFeatureRegistry;
import io.ketill.psx.Ps4Controller;
import org.hid4java.HidDevice;
import org.jetbrains.annotations.NotNull;

import static io.ketill.psx.Ps4Controller.*;

public final class HidPs4AdapterUsb extends HidPs4Adapter {

    private static final byte INPUT_ID = (byte) 0x01;
    private static final byte OUTPUT_ID = (byte) 0x05;

    private static final int INPUT_LEN = 78;
    private static final int OUTPUT_LEN = 74;

    /**
     * @param controller the controller which owns this adapter.
     * @param registry   the controller's mapped feature registry.
     * @param hidDevice  the HID device, must be open.
     * @throws NullPointerException  if {@code controller}, {@code registry},
     *                               or {@code hidDevice} are {@code} null.
     * @throws IllegalStateException if {@code hidDevice} is not open.
     */
    public HidPs4AdapterUsb(@NotNull Ps4Controller controller,
                            @NotNull MappedFeatureRegistry registry,
                            @NotNull HidDevice hidDevice) {
        super(controller, registry, hidDevice, INPUT_ID, OUTPUT_ID);
    }

    @Override
    protected byte[] generateInputReport() {
        byte[] report = new byte[INPUT_LEN];
        report[0] = INPUT_ID;
        populateInputReport(report, 1);
        return report;
    }

    @Override
    protected byte[] generateOutputReport() {
        byte[] report = new byte[OUTPUT_LEN];
        report[0] = (byte) 0xF7; /* enable all features */
        report[1] = (byte) 0x04; /* unknown */
        return report;
    }

    @Override
    protected void initAdapter() {
        this.mapDpad(BUTTON_UP, 5, DPAD_PATTERNS_UP);
        this.mapDpad(BUTTON_DOWN, 5, DPAD_PATTERNS_DOWN);
        this.mapDpad(BUTTON_LEFT, 5, DPAD_PATTERNS_LEFT);
        this.mapDpad(BUTTON_RIGHT, 5, DPAD_PATTERNS_RIGHT);

        this.mapButton(BUTTON_SQUARE, 5, 4);
        this.mapButton(BUTTON_CROSS, 5, 5);
        this.mapButton(BUTTON_CIRCLE, 5, 6);
        this.mapButton(BUTTON_TRIANGLE, 5, 7);
        this.mapButton(BUTTON_L1, 6, 0);
        this.mapButton(BUTTON_R1, 6, 1);
        this.mapButton(BUTTON_L2, 6, 2);
        this.mapButton(BUTTON_R2, 6, 3);
        this.mapButton(BUTTON_SHARE, 6, 4);
        this.mapButton(BUTTON_OPTIONS, 6, 5);
        this.mapButton(BUTTON_L_THUMB, 6, 6);
        this.mapButton(BUTTON_R_THUMB, 6, 7);
        this.mapButton(BUTTON_PS, 7, 0);
        this.mapButton(BUTTON_TPAD, 7, 1);

        this.mapStick(STICK_LS, 1, 2, 6, 6);
        this.mapStick(STICK_RS, 3, 4, 6, 7);

        this.mapTrigger(TRIGGER_LT, 8);
        this.mapTrigger(TRIGGER_RT, 9);

        this.mapMotor(MOTOR_WEAK, 3);
        this.mapMotor(MOTOR_STRONG, 4);

        this.mapLightbar(5);
    }

}
