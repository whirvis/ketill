package org.ardenus.engine.input.device.adapter.gamecube;

import java.util.Objects;

import org.ardenus.engine.input.device.GameCubeController;
import org.ardenus.engine.input.device.adapter.AdapterMapping;
import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.adapter.ButtonMapping;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.ardenus.engine.input.device.adapter.RumbleMapping;
import org.ardenus.engine.input.device.feature.Button1b;
import org.ardenus.engine.input.device.feature.Trigger1f;
import org.ardenus.engine.input.device.feature.Vibration1f;
import org.joml.Vector3f;

/**
 * A USB GameCube adapter for a Nintendo GameCube controller.
 * 
 * @see USBGameCubeDevice
 */
public class USBGameCubeControllerAdapter
		extends DeviceAdapter<GameCubeController> {

	/* @formatter: off */
	@AdapterMapping
	public static final ButtonMapping
			A = new USBGameCubeButtonMapping(GameCubeController.A, 0),
			B = new USBGameCubeButtonMapping(GameCubeController.B, 1),
			X = new USBGameCubeButtonMapping(GameCubeController.X, 2),
			Y = new USBGameCubeButtonMapping(GameCubeController.Y, 3),
			LEFT = new USBGameCubeButtonMapping(GameCubeController.LEFT, 4),
			RIGHT = new USBGameCubeButtonMapping(GameCubeController.RIGHT, 5),
			DOWN = new USBGameCubeButtonMapping(GameCubeController.DOWN, 6),
			UP = new USBGameCubeButtonMapping(GameCubeController.UP, 7),
			START = new USBGameCubeButtonMapping(GameCubeController.START, 8),
			Z = new USBGameCubeButtonMapping(GameCubeController.Z, 9),
			R = new USBGameCubeButtonMapping(GameCubeController.R, 10),
			L = new USBGameCubeButtonMapping(GameCubeController.L, 11);
	
	@AdapterMapping
	public static final AnalogMapping<?>
			LS = new USBGameCubeAnalogStickMapping(GameCubeController.LS, 0, 1,
					34, 230, 30, 232),
			RS = new USBGameCubeAnalogStickMapping(GameCubeController.RS, 2, 3,
					48, 226, 30, 218),
			LT = new USBGamecubeAnalogTriggerMapping(GameCubeController.LT, 4,
					42, 186),
			RT = new USBGamecubeAnalogTriggerMapping(GameCubeController.RT, 5,
					42, 186);
	
	@AdapterMapping
	public static final RumbleMapping
			RUMBLE = new USBGameCubeRumbleMapping(GameCubeController.RUMBLE);
	/* @formatter: on */

	private static final int BUTTON_COUNT = 12;
	private static final int ANALOG_COUNT = 6;

	private final USBGameCubeDevice device;
	private final int slot;
	private int type;
	private final boolean[] buttons;
	private final int[] analogs;
	private boolean rumbling;

	/**
	 * Constructs a new {@code USBGameCubeControllerAdapter}.
	 * 
	 * @param data
	 *            the current data from the USB adapter, update the bytes within
	 *            this array to update the data this adapter has access to.
	 * @param slot
	 *            the controller slot.
	 * @throws NullPointerException
	 *             if {@code data} is {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code data.length} is less than
	 *             {@value USBGameCubeDevice#PAYLOAD_LENGTH}; if {@code slot} is
	 *             out of range for this adapter.
	 */
	protected USBGameCubeControllerAdapter(USBGameCubeDevice device, int slot) {
		this.device = Objects.requireNonNull(device, "device");

		/*
		 * Using the slot number, it can be determined where the data begins for
		 * this specific controller. The first byte is a header byte, so that
		 * will be skipped over. Afterwards, each controller uses nine bytes to
		 * report their input data. So, the data offset is as follows.
		 */
		this.slot = slot;

		this.buttons = new boolean[BUTTON_COUNT];
		this.analogs = new int[ANALOG_COUNT];
	}

	private float getPos(int gcAxis, int min, int max) {
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
	public void isPressed(USBGameCubeButtonMapping mapping, Button1b state) {
		state.pressed = this.buttons[mapping.gcButton];
	}

	@FeatureAdapter
	public void updateAnalogStick(USBGameCubeAnalogStickMapping mapping,
			Vector3f stick) {
		stick.x = this.getPos(mapping.gcAxisX, mapping.xMin, mapping.xMax);
		stick.y = this.getPos(mapping.gcAxisY, mapping.yMin, mapping.yMax);
	}

	@FeatureAdapter
	public void updateAnalogTrigger(USBGamecubeAnalogTriggerMapping mapping,
			Trigger1f trigger) {
		float pos = this.getPos(mapping.gcAxis, mapping.min, mapping.max);
		trigger.force = (pos + 1.0F) / 2.0F;
	}

	@FeatureAdapter
	public void doRumble(USBGameCubeRumbleMapping mapping, Vibration1f motor) {
		/*
		 * TODO: At the moment, it is unknown how to make the GameCube
		 * controller rumble in accordance to a specific force. It is only known
		 * how to make it rumble, and then stop rumbling. For the time being,
		 * just make it start rumbling if the force is greater than 0 as a
		 * compromise.
		 */
		this.rumbling = motor.force > 0;
	}

	/**
	 * Returns if the controller should currently be rumbling.
	 * 
	 * @return {@code true} if the controller should currently be rumbling,
	 *         {@code false} otherwise.
	 */
	public boolean isRumbling() {
		return this.rumbling;
	}

	@Override
	public void poll() {
		byte[] data = device.getSlot(slot);
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
