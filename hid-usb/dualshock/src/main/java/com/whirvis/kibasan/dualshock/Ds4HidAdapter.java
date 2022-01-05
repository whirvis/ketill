package com.whirvis.kibasan.dualshock;

import com.whirvis.controller.Button1b;
import com.whirvis.controller.Trigger1f;
import com.whirvis.controller.Vibration1f;
import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.FeatureAdapter;
import com.whirvis.kibasan.InputException;
import com.whirvis.kibasan.psx.Ps4Controller;
import org.hid4java.HidDevice;
import org.joml.Vector3f;
import org.joml.Vector4fc;

import java.util.Objects;
import java.util.zip.CRC32;

/**
 * An adapter which maps input for a DualShock 4 HID input device.
 */
public abstract class Ds4HidAdapter extends DeviceAdapter<Ps4Controller> {

	private static final long POKE_AWAIT = 4000L;
	private static final byte[] NO_CRC_HEADER = new byte[0];

	/*
	 * For the state of the D-pad, Sony has opted to use hardcoded IDs (rather
	 * than bitfields) to represent the different directions it can be pressed
	 * in. These IDs are: 0b0000 (north), 0b0001 (north east), 0b0010 (east),
	 * 0b0011 (south east), 0b0100 (south), 0b0101 (south west), 0b0110 (west),
	 * 0b0111 (north west), and 0b1000 (released.)
	 * 
	 * Because the Ardenus Engine opts to just represent the D-pad buttons like
	 * any other buttons, it uses a pattern system as seen here. If the bits
	 * match the pattern for a D-pad, that means that specific D-pad button is
	 * pressed. For example, when the PlayStation controller reports 0b0001
	 * (north east), that indicates both UP and RIGHT are pressed.
	 */
	/* @formatter: off */
	protected static final int[]
			DPAD_PATTERNS_UP    = { 0b0000, 0b0001, 0b0111 },
			DPAD_PATTERNS_DOWN  = { 0b0011, 0b0100, 0b0101 },
			DPAD_PATTERNS_LEFT  = { 0b0101, 0b0110, 0b0111 },
			DPAD_PATTERNS_RIGHT = { 0b0001, 0b0010, 0b0011 };
	/* @formatter: on */

	/**
	 * Populates an input report with the default state of a PS4 controller.
	 * This method exists only due to the fact that input report data seems to
	 * be the same across USB and Bluetooth except for their offset.
	 * 
	 * @param report
	 *            the input report to populate.
	 * @param offset
	 *            the offset to begin writing at.
	 * @return the new offset.
	 */
	protected static int populateInputReport(byte[] report, int offset) {
		/*
		 * The next four bytes are the axes for the left and right analog
		 * sticks. Since the values of zero do not represent a stick in its
		 * default position (in the middle), these must also be set manually.
		 */
		byte middlePos = (byte) 0x7F;
		report[offset++] = middlePos;
		report[offset++] = middlePos;
		report[offset++] = middlePos;
		report[offset++] = middlePos;

		/*
		 * The final byte that must be manually updated is the byte which
		 * contains the state for the buttons. While most buttons on this
		 * controller use zero to represent released, the D-pad does not.
		 * Instead, a value of zero for the D-pad bits mean that the north
		 * button is being pressed. This is remedied by writing 0b1000 in the
		 * lower four bits, which represents released for the D-pad.
		 * 
		 * (They couldn't just have used a normal bitfield here?)
		 */
		byte padBits = (byte) 0b00001000;
		report[offset++] = padBits;

		return offset;
	}

	protected final HidDevice hid;

	private final byte inputReportId;
	private final byte[] inputReport;
	private final byte[] inputReportSink;
	private final byte outputReportId;
	private final byte[] outputReport;

	private final CRC32 crc32;
	private final byte[] crcHeader;
	private long lastChecksum;
	private long lastPokeTime;
	private boolean connected;

	/**
	 * @param hid
	 *            the HID device, must be open.
	 * @param inputReportId
	 *            the input report ID.
	 * @param outputReportId
	 *            the output report ID.
	 * @param crcHeader
	 *            the header to update the CRC signature with before writing the
	 *            output report ID. This is not always necessary, however a
	 *            {@code null} value is not permitted (use an empty array.)
	 * @throws NullPointerException
	 *             if {@code hid} or {@code crcHeader} are {@code null}.
	 * @throws InputException
	 *             if {@code hid} is not open.
	 */
	public Ds4HidAdapter(HidDevice hid, byte inputReportId, byte outputReportId,
			byte[] crcHeader) {
		this.hid = Objects.requireNonNull(hid, "hid");
		Objects.requireNonNull(crcHeader, "crcHeader");
		if (!hid.isOpen()) {
			throw new InputException("HID device not open");
		}

		this.inputReportId = inputReportId;
		this.inputReport = this.generateInputReport();
		this.inputReportSink = new byte[inputReport.length];
		this.outputReportId = outputReportId;
		this.outputReport = this.generateOutputReport();

		this.crc32 = new CRC32();
		this.crcHeader = crcHeader;
		this.lastChecksum = -1L;
		this.connected = true;
	}

	/**
	 * @param hid
	 *            the HID device.
	 * @param inputReportId
	 *            the input report ID.
	 * @param outputReportId
	 *            the output report ID.
	 * @throws NullPointerException
	 *             if {@code hid} is {@code null}.
	 * @throws InputException
	 *             if {@code hid} is not open and could not be opened.
	 */
	public Ds4HidAdapter(HidDevice hid, byte inputReportId,
			byte outputReportId) {
		this(hid, inputReportId, outputReportId, NO_CRC_HEADER);
	}

	/**
	 * It is possible that the default state of this device will not simply be
	 * an array of zeroes. If that is the case, using an empty array will result
	 * in erroneous input data until the first input data is first read. To
	 * remedy the issue, this method generates the expected initial report.
	 * 
	 * @return the generated input report.
	 * @see #generateOutputReport()
	 */
	protected abstract byte[] generateInputReport();

	/**
	 * The output report is used to send data to the controller which contains
	 * what its current state should be (lightbar color, rumble force, etc.) The
	 * array returned by this method will used for every output report (rather
	 * than generated every time a report is sent.) This is done to save memory.
	 * As such, the returned array should contain the default controller state.
	 * 
	 * @return the generated output report.
	 * @see #generateInputReport()
	 */
	protected abstract byte[] generateOutputReport();

	private boolean isPressed(Ds4ButtonMapping mapping) {
		int bits = this.inputReport[mapping.byteOffset] & 0xFF;
		return (bits & (1 << mapping.bitIndex)) != 0;
	}

	private boolean isPressed(Ds4StickMapping mapping) {
		Ds4ButtonMapping zMapping =
				(Ds4ButtonMapping) this.getMapping(mapping.feature.zButton);
		if (zMapping == null) {
			return false;
		}
		return this.isPressed(zMapping);
	}

	@Override
	public boolean isConnected() {
		return this.connected;
	}

	@FeatureAdapter
	public void isPressed(Ds4DpadMapping mapping, Button1b button) {
		int bits = this.inputReport[mapping.byteOffset] & 0xFF;
		button.pressed = mapping.hasPattern(bits);
	}

	@FeatureAdapter
	public void isPressed(Ds4ButtonMapping mapping, Button1b button) {
		button.pressed = this.isPressed(mapping);
	}

	@FeatureAdapter
	public void updateStick(Ds4StickMapping mapping, Vector3f vec) {
		int posX = this.inputReport[mapping.byteOffsetX] & 0xFF;
		int posY = this.inputReport[mapping.byteOffsetY] & 0xFF;

		/*
		 * This looks a little confusing, but all that's going on here is a
		 * little bit of normalization. First, the analog sticks are converted
		 * from a 0x00 to 0xFF scale to a 0.0F to 1.0F scale. However, this is
		 * not sufficient for the input API; as it reports analog sticks from a
		 * -1.0F to 1.0F scale.
		 * 
		 * For each analog stick, the X-axis starts at the very left (at 0.0F)
		 * and ends at the very right (1.0F.). The Y-axis starts at the very at
		 * the very top (0.0F) and ends at the very bottom (1.0F.)
		 */
		vec.x = ((posX / 255.0F) * +2.0F) - 1.0F;
		vec.y = ((posY / 255.0F) * -2.0F) + 1.0F;
		vec.z = this.isPressed(mapping) ? -1.0F : 0.0F;
	}

	@FeatureAdapter
	public void updateTrigger(Ds4TriggerMapping mapping, Trigger1f trigger) {
		int pos = this.inputReport[mapping.byteOffset] & 0xFF;
		trigger.force = pos / 255.0F;
	}

	@FeatureAdapter
	public void doRumble(Ds4RumbleMapping mapping, Vibration1f vibration) {
		byte forceByte = (byte) (vibration.force * 0xFF);
		this.outputReport[mapping.byteOffset] = forceByte;
	}

	@FeatureAdapter
	public void shineLightbar(Ds4LightbarMapping mapping, Vector4fc color) {
		int offset = mapping.byteOffset;
		float alpha = color.w() * 0xFF;
		this.outputReport[offset++] = (byte) (color.x() * alpha);
		this.outputReport[offset++] = (byte) (color.y() * alpha);
		this.outputReport[offset++] = (byte) (color.z() * alpha);
	}

	@Override
	public void poll() {
		long currentTime = System.currentTimeMillis();

		/*
		 * If the amount of data read is less than zero, that means that some
		 * sort of error has occurred (usually, it's just that the device has
		 * been disconnected.) As such, set the connected state to false and
		 * return from this function early. This is necessary to prevent
		 * erroneous input data from being reported.
		 */
		int read = hid.read(inputReportSink);
		if (read < 0) {
			this.connected = false;
			return;
		}

		/*
		 * A sink is read into just in case the controller sends an unexpected
		 * report. If the read is greater than zero, that means new input data
		 * was received. So long as the first byte of the new data matches the
		 * expected report id, copy the read bytes from the sink to the current
		 * input report. If an unexpected report is received, silently ignored
		 * it. This is to prevent erroneous input data from being calculated
		 * (and possibly crashing the adapter.)
		 */
		if (read > 0 && inputReportSink[0] == inputReportId) {
			for (int i = 0; i < read; i++) {
				this.inputReport[i] = this.inputReportSink[i];
			}
		}

		/*
		 * For the PlayStation 4 controller to accept any input, it must be
		 * given a CRC32 of the previous bytes sent in the packet. If it is
		 * absent or not correctly calculated, it will not accept the output.
		 * 
		 * While this requirement has proven to be annoying to implement, it
		 * does have another use in that it can be used to determine if an
		 * output packet need be sent at all. If the new checksum is equal to
		 * previous checksum, that means nothing need be sent.
		 */
		crc32.reset();
		crc32.update(crcHeader);
		crc32.update(outputReportId);
		crc32.update(outputReport);
		long checksum = crc32.getValue();

		/*
		 * If a certain amount of time has elapsed since the last packet was
		 * sent, send it regardless of the checksum. This ensures that the
		 * controller does not "zone out" and assume it's lost connection.
		 * Failing to poke the controller for a certain duration will cause
		 * undesirable effects (such as rumbling stopping early) to occur.
		 */
		long pokeDelta = currentTime - lastPokeTime;

		if (lastChecksum != checksum || pokeDelta >= POKE_AWAIT) {
			/*
			 * Since the checksum must be appended to the end of the original
			 * report, the bytes of the output report must be copied to a buffer
			 * with additional storage to contain the checksum.
			 */
			int offset = 0;
			byte[] message = new byte[outputReport.length + 4];
			for (int i = 0; i < outputReport.length; i++) {
				message[offset++] = this.outputReport[i];
			}

			message[offset++] = (byte) (checksum >> 0);
			message[offset++] = (byte) (checksum >> 8);
			message[offset++] = (byte) (checksum >> 16);
			message[offset++] = (byte) (checksum >> 24);

			hid.write(message, message.length, outputReportId);

			this.lastChecksum = checksum;
			this.lastPokeTime = currentTime;
		}
	}

}
