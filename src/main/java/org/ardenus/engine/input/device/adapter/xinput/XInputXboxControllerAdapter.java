package org.ardenus.engine.input.device.adapter.xinput;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.ardenus.engine.input.device.adapter.mapping.AdapterMapping;
import org.ardenus.engine.input.device.adapter.mapping.AnalogMapping;
import org.ardenus.engine.input.device.adapter.mapping.ButtonMapping;
import org.ardenus.engine.input.device.adapter.xinput.analog.XInputAnalogStickMapping;
import org.ardenus.engine.input.device.adapter.xinput.analog.XInputAnalogTriggerMapping;
import org.ardenus.engine.input.device.analog.Trigger1f;
import org.ardenus.engine.input.device.button.Button1b;
import org.ardenus.engine.input.device.controller.XboxController;
import org.joml.Vector3f;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.enums.XInputAxis;

/**
 * An X-input device adapter for an {@link XboxController}.
 *
 * @see XInputDeviceAdapter
 */
public class XInputXboxControllerAdapter
		extends XInputDeviceAdapter<XboxController> {

	/* @formatter: off */
	@AdapterMapping
	public static final AnalogMapping<?>
			LS = new XInputAnalogStickMapping(XboxController.LS,
					XInputAxis.LEFT_THUMBSTICK_X,
					XInputAxis.LEFT_THUMBSTICK_Y),
			RS = new XInputAnalogStickMapping(XboxController.RS,
					XInputAxis.RIGHT_THUMBSTICK_X,
					XInputAxis.RIGHT_THUMBSTICK_Y),
			LT = new XInputAnalogTriggerMapping(XboxController.LT,
					XInputAxis.LEFT_TRIGGER),
			RT = new XInputAnalogTriggerMapping(XboxController.RT,
					XInputAxis.RIGHT_TRIGGER);
	
	@AdapterMapping
	public static final ButtonMapping
			A = new XInputButtonMapping(XboxController.A, "a"),
			B = new XInputButtonMapping(XboxController.B, "b"),
			X = new XInputButtonMapping(XboxController.X, "x"),
			Y = new XInputButtonMapping(XboxController.Y, "y"),
			LB = new XInputButtonMapping(XboxController.LB, "lShoulder"),
			RB = new XInputButtonMapping(XboxController.RB, "rShoulder"),
			GUIDE = new XInputButtonMapping(XboxController.GUIDE, "guide"),
			START = new XInputButtonMapping(XboxController.START, "start"),
			THUMB_L = new XInputButtonMapping(XboxController.THUMB_L, "lThumb"),
			THUMB_R = new XInputButtonMapping(XboxController.THUMB_R, "rThumb"),
			UP = new XInputButtonMapping(XboxController.UP, "up"),
			RIGHT = new XInputButtonMapping(XboxController.RIGHT, "right"),
			DOWN = new XInputButtonMapping(XboxController.DOWN, "down"),
			LEFT = new XInputButtonMapping(XboxController.LEFT, "left");
	/* @formatter: on */

	private XInputButtons buttons;
	private XInputAxes axes;

	/**
	 * Constructs a new {@code XInputXboxControllerAdapter}.
	 * 
	 * @param xinput
	 *            the X-input device.
	 * @throws NullPointerException
	 *             if {@code xinput} is {@code null}.
	 * @throws InputException
	 *             if an input error occurs.
	 */
	public XInputXboxControllerAdapter(XInputDevice xinput) {
		super(xinput);
	}

	protected boolean isPressed(XInputAnalogStickMapping mapping) {
		XInputButtonMapping zMapping =
				(XInputButtonMapping) this.getMapping(mapping.feature.zButton);
		if (zMapping == null) {
			return false;
		}
		return zMapping.isPressed(buttons);
	}

	@FeatureAdapter
	public void updateAnalogStick(XInputAnalogStickMapping mapping,
			Vector3f stick) {
		stick.x = axes.get(mapping.xAxis);
		stick.y = axes.get(mapping.yAxis);
		stick.z = this.isPressed(mapping) ? -1.0F : 0.0F;
	}

	@FeatureAdapter
	public void updateAnalogTrigger(XInputAnalogTriggerMapping mapping,
			Trigger1f trigger) {
		trigger.force = axes.get(mapping.triggerAxis);
	}

	@FeatureAdapter
	public void isPressed(XInputButtonMapping mapping, Button1b button) {
		button.pressed = mapping.isPressed(buttons);
	}

	@Override
	public void poll() {
		super.poll();

		XInputComponents comps = xinput.getComponents();
		this.axes = comps.getAxes();
		this.buttons = comps.getButtons();
	}

}
