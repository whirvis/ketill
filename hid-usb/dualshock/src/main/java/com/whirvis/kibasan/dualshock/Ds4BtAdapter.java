package com.whirvis.kibasan.dualshock;

import com.whirvis.kibasan.MappedFeatureRegistry;
import com.whirvis.kibasan.psx.Ps4Controller;
import org.hid4java.HidDevice;
import org.jetbrains.annotations.NotNull;

import static com.whirvis.kibasan.psx.Ps4Controller.*;

public class Ds4BtAdapter extends Ds4HidAdapter {

    /* @formatter:off */
	private static final byte
			INPUT_ID      = (byte) 0x11,
			OUTPUT_ID     = (byte) 0x15;

	private static final int
			INPUT_LENGTH  = 78,
			OUTPUT_LENGTH = 329;

	private static final byte[]
			OUTPUT_HEADER = {
				(byte) 0xC0, /* poll rate */
				(byte) 0xA0, /* unknown */
				(byte) 0xF7, /* enable all features */
				(byte) 0x04 /* unknown */
			},

			CHECKSUM_HEADER = new byte[] {
				(byte) 0xA2 /* data output */
			};
	/* @formatter:on */

    public Ds4BtAdapter(HidDevice hid) {
        super(hid, INPUT_ID, OUTPUT_ID, CHECKSUM_HEADER);
    }

    @Override
    protected void initAdapter(@NotNull Ps4Controller device,
                               @NotNull MappedFeatureRegistry registry) {
        this.mapDpad(registry, BUTTON_UP, 7, DPAD_PATTERNS_UP);
        this.mapDpad(registry, BUTTON_DOWN, 7, DPAD_PATTERNS_DOWN);
        this.mapDpad(registry, BUTTON_LEFT, 7, DPAD_PATTERNS_LEFT);
        this.mapDpad(registry, BUTTON_RIGHT, 7, DPAD_PATTERNS_RIGHT);

        this.mapButton(registry, BUTTON_SQUARE, 7, 4);
        this.mapButton(registry, BUTTON_CROSS, 7, 5);
        this.mapButton(registry, BUTTON_CIRCLE, 7, 6);
        this.mapButton(registry, BUTTON_TRIANGLE, 7, 7);
        this.mapButton(registry, BUTTON_L1, 8, 0);
        this.mapButton(registry, BUTTON_R1, 8, 1);
        this.mapButton(registry, BUTTON_L2, 8, 2);
        this.mapButton(registry, BUTTON_R2, 8, 3);
        this.mapButton(registry, BUTTON_SHARE, 8, 4);
        this.mapButton(registry, BUTTON_OPTIONS, 8, 5);
        this.mapButton(registry, BUTTON_L_THUMB, 8, 6);
        this.mapButton(registry, BUTTON_R_THUMB, 8, 7);
        this.mapButton(registry, BUTTON_PS, 9, 0);
        this.mapButton(registry, BUTTON_TPAD, 9, 1);

        this.mapStick(registry, STICK_LS, 3, 4, 8, 6);
        this.mapStick(registry, STICK_LS, 5, 6, 8, 7);

        this.mapTrigger(registry, TRIGGER_LT, 10);
        this.mapTrigger(registry, TRIGGER_RT, 11);

        this.mapMotor(registry, MOTOR_WEAK, 5);
        this.mapMotor(registry, MOTOR_STRONG, 6);

        this.mapLightbar(registry, FEATURE_LIGHTBAR, 7);
    }

    @Override
    protected byte[] generateInputReport() {
        int offset = 0;
        byte[] report = new byte[INPUT_LENGTH];

        report[offset++] = INPUT_ID;
        report[offset++] = (byte) 0xC0; /* poll rate */
        report[offset++] = (byte) 0x00; /* unknown */
        populateInputReport(report, offset);

        return report;
    }

    @Override
    protected byte[] generateOutputReport() {
        int offset = 0;
        byte[] report = new byte[OUTPUT_LENGTH];

        for (byte b : OUTPUT_HEADER) {
            report[offset++] = b;
        }

        return report;
    }

}
