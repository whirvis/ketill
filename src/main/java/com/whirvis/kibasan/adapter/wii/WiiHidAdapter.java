package com.whirvis.kibasan.adapter.wii;

import java.util.Objects;

import org.hid4java.HidDevice;

import com.whirvis.kibasan.InputException;
import com.whirvis.kibasan.Wiimote;
import com.whirvis.kibasan.adapter.AdapterMapping;
import com.whirvis.kibasan.adapter.DeviceAdapter;
import com.whirvis.kibasan.adapter.FeatureAdapter;
import com.whirvis.kibasan.feature.Button1b;
import com.whirvis.kibasan.feature.PlayerLed1i;
import com.whirvis.kibasan.feature.Vibration1f;

/**
 * An adapter which maps input for a Nintendo Wiimote HID input device.
 */
public class WiiHidAdapter extends DeviceAdapter<Wiimote> {

	/* @formatter: off */
	private static final byte
			RUMBLE_ID = (byte) 0x10,
			SHINE_LED_ID = (byte) 0x11,
			DATA_REPORT_MODE_ID = (byte) 0x12,
			STATUS_REQUEST_ID = (byte) 0x15,
			STATUS_REPORT_ID = (byte) 0x20;
	
	private static final InputChannel
			CHANNEL_CORE = new CoreInputChannel();
	
	public static final WiiButtonMapping
			LEFT_N = new WiiButtonMapping(Wiimote.LEFT, 0, 0),
			RIGHT_N = new WiiButtonMapping(Wiimote.RIGHT, 0, 1),
			DOWN_N = new WiiButtonMapping(Wiimote.DOWN, 0, 2),
			UP_N = new WiiButtonMapping(Wiimote.UP, 0, 3);
	
	public static final WiiButtonMapping
			RIGHT_S = new WiiButtonMapping(Wiimote.RIGHT, 0, 0),
			LEFT_S = new WiiButtonMapping(Wiimote.LEFT, 0, 1),
			UP_S = new WiiButtonMapping(Wiimote.UP, 0, 2),
			DOWN_S = new WiiButtonMapping(Wiimote.DOWN, 0, 3);
	
	public static final WiiButtonMapping
			DOWN_W = new WiiButtonMapping(Wiimote.DOWN, 0, 0),
			UP_W = new WiiButtonMapping(Wiimote.UP, 0, 1),
			RIGHT_W = new WiiButtonMapping(Wiimote.RIGHT, 0, 2),
			LEFT_W = new WiiButtonMapping(Wiimote.LEFT, 0, 3);
	
	public static final WiiButtonMapping
			UP_E = new WiiButtonMapping(Wiimote.UP, 0, 0),
			DOWN_E = new WiiButtonMapping(Wiimote.DOWN, 0, 1),
			LEFT_E = new WiiButtonMapping(Wiimote.LEFT, 0, 2),
			RIGHT_E = new WiiButtonMapping(Wiimote.RIGHT, 0, 3);
	
	@AdapterMapping
	public static final WiiButtonMapping
			PLUS = new WiiButtonMapping(Wiimote.PLUS, 0, 4),
			TWO = new WiiButtonMapping(Wiimote.TWO, 1, 0),
			ONE = new WiiButtonMapping(Wiimote.ONE, 1, 1),
			B = new WiiButtonMapping(Wiimote.B, 1, 2),
			A = new WiiButtonMapping(Wiimote.A, 1, 3),
			MINUS = new WiiButtonMapping(Wiimote.MINUS, 1, 4),
			HOME = new WiiButtonMapping(Wiimote.HOME, 1, 7);
	
	@AdapterMapping
	public static final WiiRumbleMapping
			RUMBLE = new WiiRumbleMapping(Wiimote.RUMBLE);

	@AdapterMapping
	public static final WiiPlayerLedMapping
			PLAYER_LED = new WiiPlayerLedMapping(Wiimote.PLAYER_LED);
	/* @formatter: on */

	private final HidDevice hid;
	private final WiimoteRegister register;
	private final byte[] report;

	private boolean requestedStatus;
	private InputChannel channel;
	private boolean validInput;
	private long lastValidInput;
	
	private boolean wasRumbling;
	private int ledNumber;
	private boolean connected;

	/**
	 * @param hid
	 *            the HID device, must be open.
	 * @throws NullPointerException
	 *             if {@code hid} is {@code null}.
	 * @throws InputException
	 *             if {@code hid} is not open.
	 */
	public WiiHidAdapter(HidDevice hid) {
		this.hid = Objects.requireNonNull(hid, "hid");
		if (!hid.isOpen()) {
			throw new InputException("HID device not open");
		}
		this.register = new WiimoteRegister(hid);
		this.report = new byte[64];
	}

	private void requestStatus() {
		hid.write(new byte[] {
				0x00 /* reset state */
		}, 1, STATUS_REQUEST_ID);
	}

	private void useInputChannel(InputChannel channel) {
		Objects.requireNonNull(channel, "channel");

		this.validInput = false; /* wait for next input report */
		this.lastValidInput = System.currentTimeMillis();
		this.channel = channel;

		/*
		 * There was once a check here that ensured the input channel packet was
		 * sent only if it was identical from the old one. However, this micro
		 * optimization broke the adapter. This because the Wiimote sometimes
		 * needs to be told which input channel to use again. This check would
		 * prevent the adapter from reminding the Wiimote which input channel to
		 * use, causing input reports to cease.
		 */
		hid.write(new byte[] {
				(byte) (channel.delta ? 0b000 : 0b100), (byte) channel.reportId
		}, 2, DATA_REPORT_MODE_ID);
	}

	private boolean isInputReady() {
		/*
		 * This looks redundant at first sight. However, it's possible that in
		 * the future the parameters for when input state should be updated is
		 * changed. Doing it this way helps prevent bugs that are due to a bad
		 * set of copy pastes.
		 */
		return this.validInput;
	}

	@FeatureAdapter
	public void isPressed(WiiButtonMapping mapping, Button1b button) {
		if (this.isInputReady()) {
			byte[] core = channel.getData(InputChannel.CORE);
			int bits = core != null ? core[mapping.byteOffset] : 0;
			button.pressed = (bits & (1 << mapping.bitIndex)) != 0;
		} else {
			button.pressed = false;
		}
	}

	@FeatureAdapter
	public void doRumble(WiiRumbleMapping mapping, Vibration1f vibration) {
		boolean rumbling = vibration.force > 0.0F;
		if (wasRumbling != rumbling) {
			hid.write(new byte[] {
					(byte) (rumbling ? 0x01 : 0x00)
			}, 1, RUMBLE_ID);
			this.wasRumbling = rumbling;
		}
	}

	@FeatureAdapter
	public void shineLed(WiiPlayerLedMapping mapping, PlayerLed1i led) {
		if (this.ledNumber != led.number) {
			hid.write(new byte[] {
					(byte) (led.number << 4)
			}, 1, SHINE_LED_ID);
			this.ledNumber = led.number;
		}
	}

	@Override
	public boolean isConnected() {
		return this.connected;
	}

	@Override
	public void poll() {
		long currentTime = System.currentTimeMillis();

		/*
		 * As soon as the Wiimote connects, a status request is sent. This is
		 * done to get the current status of the Wiimote before setting the
		 * input channel. The input channel that is used depends on a variety of
		 * factors. However, the main decider is whether or not the Wiimote has
		 * any extensions connected.
		 */
		if (!requestedStatus) {
			this.requestStatus();
			this.requestedStatus = true;
		}

		if (channel != null) {
			long elapsed = currentTime - lastValidInput;

			/*
			 * If the Wiimote is only sending input reports when its state
			 * changes, there's likely no issue. As such, only send a status
			 * request if no input has arrived after a full second. This is
			 * technically not necessary, but it makes for good hygiene.
			 */
			if (channel.delta && elapsed >= 1000L) {
				this.requestStatus();
				this.lastValidInput = currentTime;
			}

			/*
			 * On the other hand, if the Wiimote is sending input reports
			 * persistently, and more than 100ms have passed since the last
			 * valid input report has arrived, something has gone wrong.
			 * 
			 * This has been observed to occur when someone intentionally
			 * inserts or unplugs an extension in an awkward manner (e.g.,
			 * connecting to the pins just barely and then unplugging.)
			 */
			else if (!channel.delta && elapsed >= 100L) {
				this.requestStatus();
				this.lastValidInput = currentTime;
				log.warn(String.format("Input data on non-delta input "
						+ "channel 0x%02X has not arrived in over %dms.",
						channel.reportId, elapsed));
			}
		}

		/*
		 * If the amount of data read is less than zero, that means that some
		 * sort of error has occurred (usually, it's just that the device has
		 * been disconnected.) As such, set the connected state to false and
		 * return from this function early. This is necessary to prevent
		 * erroneous input data from being reported.
		 */
		int read = hid.read(report);
		if (read < 0) {
			this.connected = false;
			return;
		} else if (read == 0) {
			return;
		}

		int reportId = report[0];

		/*
		 * When the report ID is that of the input channel, it means that input
		 * data has been received. If the handle report method returns false, it
		 * means the data it received was invalid. This is normal, and the
		 * course of action is to simply ignore the report.
		 */
		if (channel != null && reportId == channel.reportId) {
			this.validInput = channel.handleReport(report);
			if (validInput) {
				this.lastValidInput = currentTime;
			}
		}

		/*
		 * When a status report is received, it means either the status was
		 * requested or an extension controller has been connected. Whenever the
		 * Wiimote sends a status report, it will stop sending any input data
		 * until it is told which input channel to use again.
		 * 
		 * As such, poll the Wiimote for its current extension. The method
		 * handling this will determine which input channel to use based on the
		 * current Wiimote extensions.
		 */
		else if (reportId == STATUS_REPORT_ID) {
			this.connected = true;

			/*
			 * The input data must be reset so no more is accepted until the
			 * current state of the Wiimote has been verified. The value for
			 * lastValidInput is set to currentTime so the Wiimote has an
			 * opportunity to verify its current state (before the adapter
			 * assumes something has gone wrong.)
			 */
			this.channel = null;
			this.validInput = false;
			this.lastValidInput = currentTime;

			/*
			 * The Wiimote will not report any input after sending a status
			 * report until it is told which input channel to use again.
			 */
			this.useInputChannel(CHANNEL_CORE);
		}

		/*
		 * When a register report is received, shuffle the current report off to
		 * the register handler. This handler stitches together both EEPROM and
		 * register memory requested by the adapter.
		 */
		else if (register.isDataReport(report)) {
			register.handleData(report);
		}
	}

}
