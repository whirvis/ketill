package com.whirvis.kibasan.adapter.dualshock;

import org.hid4java.HidDevice;

import com.whirvis.kibasan.Ps4Controller;
import com.whirvis.kibasan.adapter.AdapterMapping;

public class Ds4UsbAdapter extends Ds4HidAdapter {

	private static final byte INPUT_ID = (byte) 0x01;
	private static final byte OUTPUT_ID = (byte) 0x05;

	private static final int INPUT_LEN = 78;
	private static final int OUTPUT_LEN = 74;

	private static final byte[] OUTPUT_HEADER = new byte[] {
			(byte) 0xF7, /* enable all features */
			(byte) 0x04 /* unknown */
	};

	/* @formatter: off */
	@AdapterMapping
	public static final Ds4DpadMapping
			UP = new Ds4DpadMapping(Ps4Controller.UP,
					5, DPAD_PATTERNS_UP),
			DOWN = new Ds4DpadMapping(Ps4Controller.DOWN,
					5, DPAD_PATTERNS_DOWN),
			LEFT = new Ds4DpadMapping(Ps4Controller.LEFT,
					5, DPAD_PATTERNS_LEFT), 
			RIGHT = new Ds4DpadMapping(Ps4Controller.RIGHT,
					5, DPAD_PATTERNS_RIGHT);
	
	@AdapterMapping
	public static final Ds4ButtonMapping
			SQUARE = new Ds4ButtonMapping(Ps4Controller.SQUARE, 5, 4),
			CROSS = new Ds4ButtonMapping(Ps4Controller.CROSS, 5, 5),
			CIRCLE = new Ds4ButtonMapping(Ps4Controller.CIRCLE, 5, 6),
			TRIANGLE = new Ds4ButtonMapping(Ps4Controller.TRIANGLE, 5, 7),
			L1 = new Ds4ButtonMapping(Ps4Controller.L1, 6, 0),
			R1 = new Ds4ButtonMapping(Ps4Controller.R1, 6, 1),
			L2 = new Ds4ButtonMapping(Ps4Controller.L2, 6, 2),
			R2 = new Ds4ButtonMapping(Ps4Controller.R2, 6, 3),
			SHARE = new Ds4ButtonMapping(Ps4Controller.SHARE, 6, 4),
			OPTIONS = new Ds4ButtonMapping(Ps4Controller.OPTIONS, 6, 5),
			THUMB_L = new Ds4ButtonMapping(Ps4Controller.THUMB_L, 6, 6),
			THUMB_R = new Ds4ButtonMapping(Ps4Controller.THUMB_R, 6, 7),
			PS = new Ds4ButtonMapping(Ps4Controller.PS, 7, 0),
			TPAD = new Ds4ButtonMapping(Ps4Controller.TPAD, 7, 1);
	
	@AdapterMapping
	public static final Ds4StickMapping
			LS = new Ds4StickMapping(Ps4Controller.LS, 1, 2),
			RS = new Ds4StickMapping(Ps4Controller.RS, 3, 4);
			
	@AdapterMapping
	public static final Ds4TriggerMapping
			LT = new Ds4TriggerMapping(Ps4Controller.LT, 8),
			RT = new Ds4TriggerMapping(Ps4Controller.RT, 9);
	
	@AdapterMapping
	public static final Ds4RumbleMapping
			RUMBLE_WEAK = new Ds4RumbleMapping(Ps4Controller.RUMBLE_WEAK, 3),
			RUMBLE_STRONG = new Ds4RumbleMapping(Ps4Controller.RUMBLE_STRONG, 4);
	
	@AdapterMapping
	public static final Ds4LightbarMapping
			LIGHTBAR = new Ds4LightbarMapping(Ps4Controller.LIGHTBAR, 5);
	/* @formatter: on */

	public Ds4UsbAdapter(HidDevice hid) {
		super(hid, INPUT_ID, OUTPUT_ID);
	}

	@Override
	protected byte[] generateInputReport() {
		int offset = 0;
		byte[] report = new byte[INPUT_LEN];

		report[offset++] = INPUT_ID;
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
