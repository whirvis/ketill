package com.whirvis.kibasan.dualshock;

import com.whirvis.kibasan.AdapterMapping;
import com.whirvis.kibasan.psx.Ps4Controller;
import org.hid4java.HidDevice;

public class Ds4BtAdapter extends Ds4HidAdapter {

	private static final byte INPUT_ID = (byte) 0x11;
	private static final byte OUTPUT_ID = (byte) 0x15;

	private static final int INPUT_LEN = 78;
	private static final int OUTPUT_LEN = 329;

	private static final byte[] OUTPUT_HEADER = {
			(byte) 0xC0, /* poll rate */
			(byte) 0xA0, /* unknown */
			(byte) 0xF7, /* enable all features */
			(byte) 0x04 /* unknown */
	};

	private static final byte[] CHECKSUM_HEADER = new byte[] {
			(byte) 0xA2 /* data output */
	};

	/* @formatter: off */
	@AdapterMapping
	public static final Ds4DpadMapping
			UP = new Ds4DpadMapping(Ps4Controller.UP, 7,
					DPAD_PATTERNS_UP),
			DOWN = new Ds4DpadMapping(Ps4Controller.DOWN, 7,
					DPAD_PATTERNS_DOWN),
			LEFT = new Ds4DpadMapping(Ps4Controller.LEFT, 7,
					DPAD_PATTERNS_LEFT),
			RIGHT = new Ds4DpadMapping(Ps4Controller.RIGHT, 7,
					DPAD_PATTERNS_RIGHT);
	
	@AdapterMapping
	public static final Ds4ButtonMapping
			SQUARE = new Ds4ButtonMapping(Ps4Controller.SQUARE, 7, 4),
			CROSS = new Ds4ButtonMapping(Ps4Controller.CROSS, 7, 5),
			CIRCLE = new Ds4ButtonMapping(Ps4Controller.CIRCLE, 7, 6),
			TRIANGLE = new Ds4ButtonMapping(Ps4Controller.TRIANGLE, 7, 7),
			L1 = new Ds4ButtonMapping(Ps4Controller.L1, 8, 0),
			R1 = new Ds4ButtonMapping(Ps4Controller.R1, 8, 1),
			L2 = new Ds4ButtonMapping(Ps4Controller.L2, 8, 2),
			R2 = new Ds4ButtonMapping(Ps4Controller.R2, 8, 3),
			SHARE = new Ds4ButtonMapping(Ps4Controller.SHARE, 8, 4),
			OPTIONS = new Ds4ButtonMapping(Ps4Controller.OPTIONS, 8, 5),
			THUMB_L = new Ds4ButtonMapping(Ps4Controller.THUMB_L, 8, 6),
			THUMB_R = new Ds4ButtonMapping(Ps4Controller.THUMB_R, 8, 7),
			PS = new Ds4ButtonMapping(Ps4Controller.PS, 9, 0),
			TPAD = new Ds4ButtonMapping(Ps4Controller.TPAD, 9, 1);
	
	@AdapterMapping
	public static final Ds4StickMapping
			LS = new Ds4StickMapping(Ps4Controller.LS, 3, 4),
			RS = new Ds4StickMapping(Ps4Controller.RS, 5, 6);
			
	@AdapterMapping
	public static final Ds4TriggerMapping
			LT = new Ds4TriggerMapping(Ps4Controller.LT, 10),
			RT = new Ds4TriggerMapping(Ps4Controller.RT, 11);

	@AdapterMapping
	public static final Ds4RumbleMapping
			RUMBLE_WEAK = new Ds4RumbleMapping(Ps4Controller.RUMBLE_WEAK, 5),
			RUMBLE_STRONG = new Ds4RumbleMapping(Ps4Controller.RUMBLE_STRONG, 6);
	
	@AdapterMapping
	public static final Ds4LightbarMapping
			LIGHTBAR = new Ds4LightbarMapping(Ps4Controller.LIGHTBAR, 7);
	/* @formatter: on */

	public Ds4BtAdapter(HidDevice hid) {
		super(hid, INPUT_ID, OUTPUT_ID, CHECKSUM_HEADER);
	}

	@Override
	protected byte[] generateInputReport() {
		int offset = 0;
		byte[] report = new byte[INPUT_LEN];

		report[offset++] = INPUT_ID;
		report[offset++] = (byte) 0xC0; /* poll rate */
		report[offset++] = (byte) 0x00; /* unknown */
		offset = populateInputReport(report, offset);

		return report;
	}

	@Override
	protected byte[] generateOutputReport() {
		int offset = 0;
		byte[] report = new byte[OUTPUT_LEN];

		for (int i = 0; i < OUTPUT_HEADER.length; i++) {
			report[offset++] = OUTPUT_HEADER[i];
		}

		return report;
	}

}
