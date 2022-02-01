package com.whirvis.ketill.dualshock;

import com.whirvis.ketill.MappedFeatureRegistry;
import com.whirvis.ketill.psx.Ps4Controller;
import org.hid4java.HidDevice;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.whirvis.ketill.psx.Ps4Controller.*;

public class Ds4UsbAdapter extends Ds4HidAdapter {

    /* @formatter:off */
    private static final byte
            INPUT_ID      = (byte) 0x01,
            OUTPUT_ID     = (byte) 0x05;

    private static final int
            INPUT_LENGTH  = 78,
            OUTPUT_LENGTH = 74;

    private static final byte[]
            OUTPUT_HEADER = new byte[] {
                    (byte) 0xF7, /* enable all features */
                    (byte) 0x04 /* unknown */
            };
    /* @formatter:on */

    public Ds4UsbAdapter(HidDevice hid) {
        super(hid, INPUT_ID, OUTPUT_ID);
    }

    @Override
    protected void initAdapter(@NotNull Ps4Controller device,
                               @NotNull MappedFeatureRegistry registry) {
        this.mapDpad(registry, BUTTON_UP, 5, DPAD_PATTERNS_UP);
        this.mapDpad(registry, BUTTON_DOWN, 5, DPAD_PATTERNS_DOWN);
        this.mapDpad(registry, BUTTON_LEFT, 5, DPAD_PATTERNS_LEFT);
        this.mapDpad(registry, BUTTON_RIGHT, 5, DPAD_PATTERNS_RIGHT);

        this.mapButton(registry, BUTTON_SQUARE, 5, 4);
        this.mapButton(registry, BUTTON_CROSS, 5, 5);
        this.mapButton(registry, BUTTON_CIRCLE, 5, 6);
        this.mapButton(registry, BUTTON_TRIANGLE, 5, 7);
        this.mapButton(registry, BUTTON_L1, 6, 0);
        this.mapButton(registry, BUTTON_R1, 6, 1);
        this.mapButton(registry, BUTTON_L2, 6, 2);
        this.mapButton(registry, BUTTON_R2, 6, 3);
        this.mapButton(registry, BUTTON_SHARE, 6, 4);
        this.mapButton(registry, BUTTON_OPTIONS, 6, 5);
        this.mapButton(registry, BUTTON_L_THUMB, 6, 6);
        this.mapButton(registry, BUTTON_R_THUMB, 6, 7);
        this.mapButton(registry, BUTTON_PS, 7, 0);
        this.mapButton(registry, BUTTON_TPAD, 7, 1);

        this.mapStick(registry, STICK_LS, 1, 2, 6, 6);
        this.mapStick(registry, STICK_RS, 3, 4, 6, 7);

        this.mapTrigger(registry, TRIGGER_LT, 8);
        this.mapTrigger(registry, TRIGGER_RT, 9);

        this.mapMotor(registry, MOTOR_WEAK, 3);
        this.mapMotor(registry, MOTOR_STRONG, 4);

        this.mapLightbar(registry, FEATURE_LIGHTBAR, 5);
    }

    @Override
    protected byte[] generateInputReport() {
        int offset = 0;
        byte[] report = new byte[INPUT_LENGTH];
        report[offset++] = INPUT_ID;
        populateInputReport(report, offset);
        return report;
    }

    @Override
    protected byte[] generateOutputReport() {
        return Arrays.copyOf(OUTPUT_HEADER, OUTPUT_LENGTH);
    }

}
