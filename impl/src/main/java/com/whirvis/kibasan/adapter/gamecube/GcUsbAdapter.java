package com.whirvis.kibasan.adapter.gamecube;

import com.whirvis.controller.Button1b;
import com.whirvis.controller.Trigger1f;
import com.whirvis.controller.Vibration1f;
import com.whirvis.kibasan.AdapterMapping;
import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.FeatureAdapter;
import com.whirvis.kibasan.gc.GcController;
import org.joml.Vector3f;

import java.util.Objects;

/**
 * A USB GameCube adapter for a Nintendo GameCube controller.
 * 
 * @see GcUsbDevice
 */
public class GcUsbAdapter extends DeviceAdapter<GcController> {

	/* @formatter: off */
	@AdapterMapping
	public static final GcButtonMapping
			A = new GcButtonMapping(GcController.A, 0),
			B = new GcButtonMapping(GcController.B, 1),
			X = new GcButtonMapping(GcController.X, 2),
			Y = new GcButtonMapping(GcController.Y, 3),
			LEFT = new GcButtonMapping(GcController.LEFT, 4),
			RIGHT = new GcButtonMapping(GcController.RIGHT, 5),
			DOWN = new GcButtonMapping(GcController.DOWN, 6),
			UP = new GcButtonMapping(GcController.UP, 7),
			START = new GcButtonMapping(GcController.START, 8),
			Z = new GcButtonMapping(GcController.Z, 9),
			R = new GcButtonMapping(GcController.R, 10),
			L = new GcButtonMapping(GcController.L, 11);
	
	@AdapterMapping
	public static final GcStickMapping
			LS = new GcStickMapping(GcController.LS, 0, 1,
					34, 230, 30, 232),
			RS = new GcStickMapping(GcController.RS, 2, 3,
					48, 226, 30, 218);
			
	@AdapterMapping
	public static final GcTriggerMapping
			LT = new GcTriggerMapping(GcController.LT, 4,
					42, 186),
			RT = new GcTriggerMapping(GcController.RT, 5,
					42, 186);
	
	@AdapterMapping
	public static final GcRumbleMapping
			RUMBLE = new GcRumbleMapping(GcController.RUMBLE);
	/* @formatter: on */

	private static final int BUTTON_COUNT = 12;
	private static final int ANALOG_COUNT = 6;

	private final GcUsbDevice device;
	private final int slot;
	private int type;
	private final boolean[] buttons;
	private final int[] analogs;
	private boolean rumbling;

	/**
	 * @param device
	 *            the USB adapter this controller belongs to.
	 * @param slot
	 *            the controller slot.
	 * @throws NullPointerException
	 *             if {@code data} is {@code null}.
	 */
	protected GcUsbAdapter(GcUsbDevice device, int slot) {
		this.device = Objects.requireNonNull(device, "device");
		this.slot = slot;
		this.buttons = new boolean[BUTTON_COUNT];
		this.analogs = new int[ANALOG_COUNT];
	}

	private float getNormal(int gcAxis, int min, int max) {
		int pos = analogs[gcAxis];

		/*
		 * It's not uncommon for an axis to go one or two points outside of
		 * their usual minimum or maximum values. Clamping them to will prevent
		 * return values outside the -1.0F to 1.0F range.
		 */
		if (pos < min) {
			pos = min;
		} else if (pos > max) {
			pos = max;
		}

		float mid = (max - min) / 2.0F;
		return (pos - min - mid) / mid;
	}

	@Override
	public boolean isConnected() {
		return this.type > 0;
	}

	@FeatureAdapter
	public void isPressed(GcButtonMapping mapping, Button1b button) {
		button.pressed = this.buttons[mapping.gcButton];
	}

	@FeatureAdapter
	public void updateStick(GcStickMapping mapping, Vector3f stick) {
		stick.x = this.getNormal(mapping.gcAxisX, mapping.xMin, mapping.xMax);
		stick.y = this.getNormal(mapping.gcAxisY, mapping.yMin, mapping.yMax);
	}

	@FeatureAdapter
	public void updateTrigger(GcTriggerMapping mapping, Trigger1f trigger) {
		float pos = this.getNormal(mapping.gcAxis, mapping.min, mapping.max);
		trigger.force = (pos + 1.0F) / 2.0F;
	}

	@FeatureAdapter
	public void doRumble(GcRumbleMapping mapping, Vibration1f motor) {
		this.rumbling = motor.force > 0;
	}

	public boolean isRumbling() {
		/*
		 * It is possible that the program disconnects from the adapter while
		 * the controller should still be rumbling. Checking if the controller
		 * is connected is an easy to tell if it should stop rumbling.
		 */
		return this.isConnected() && this.rumbling;
	}

	@Override
	public void poll() {
		byte[] data = device.getSlotData(slot);
		int offset = 0;

		/*
		 * The first byte is the current controller type. This will be used to
		 * determine if the controller is connected, and whether or not it self
		 * reports to be wireless. Wireless controllers are usually Wavebird
		 * controllers. However, there is no guarantee for this.
		 */
		this.type = (data[offset++] & 0xFF) >> 4;

		/*
		 * The next two bytes of the data payload are the button states. Each
		 * button state is stored in a single bit for the next two bytes. As
		 * such, bit shifting is required to determine if a button is pressed.
		 */
		short buttonStates = 0;
		buttonStates |= (data[offset++] & 0xFF);
		buttonStates |= (data[offset++] & 0xFF) << 8;
		for (int i = 0; i < buttons.length; i++) {
			this.buttons[i] = (buttonStates & (1 << i)) != 0;
		}

		/*
		 * Each analog axis value is stored in a single byte. The amount of
		 * bytes read here is determined by the amount of axes there are on the
		 * controller. For the Nintendo GameCube controller, it is six.
		 */
		for (int i = 0; i < analogs.length; i++) {
			this.analogs[i] = data[offset++] & 0xFF;
		}
	}

}
