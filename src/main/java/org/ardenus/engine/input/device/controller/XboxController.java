package org.ardenus.engine.input.device.controller;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.AnalogPresent;
import org.ardenus.engine.input.device.ButtonPresent;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.analog.AnalogStick;
import org.ardenus.engine.input.device.analog.AnalogTrigger;

/**
 * A Microsoft XBOX controller.
 * 
 * @see Controller
 */
public class XboxController extends Controller {

	@ButtonPresent
	public static final ControllerButton BUTTON_A = new ControllerButton("A"),
			BUTTON_B = new ControllerButton("B"),
			BUTTON_X = new ControllerButton("X"),
			BUTTON_Y = new ControllerButton("Y"),
			BUTTON_LB = new ControllerButton("LB"),
			BUTTON_RB = new ControllerButton("RB"),
			BUTTON_MENU = new ControllerButton("Menu"),
			BUTTON_PAUSE = new ControllerButton("Pause"),
			BUTTON_LS = new ControllerButton("LS"),
			BUTTON_RS = new ControllerButton("RS"),
			BUTTON_UP = new ControllerButton("Up", Direction.UP),
			BUTTON_DOWN = new ControllerButton("Down", Direction.DOWN),
			BUTTON_LEFT = new ControllerButton("Left", Direction.LEFT),
			BUTTON_RIGHT = new ControllerButton("Right", Direction.RIGHT);

	@AnalogPresent
	public static final AnalogTrigger TRIGGER_L = new AnalogTrigger("LT"),
			TRIGGER_R = new AnalogTrigger("RT");

	@AnalogPresent
	public static final AnalogStick STICK_L = new AnalogStick("LS", BUTTON_LS),
			STICK_R = new AnalogStick("RS", BUTTON_RS);

	/**
	 * Constructs a new {@code XboxController}.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public XboxController(DeviceAdapter<XboxController> adapter) {
		super(adapter);
	}

}
