package org.ardenus.engine.input.device.adapter.xinput;

import org.ardenus.engine.input.device.XboxController;
import org.ardenus.engine.input.device.adapter.AdapterMapping;
import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.adapter.ButtonMapping;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.ardenus.engine.input.device.adapter.RumbleMapping;
import org.ardenus.engine.input.device.feature.Button1b;
import org.ardenus.engine.input.device.feature.Trigger1f;
import org.ardenus.engine.input.device.feature.Vibration1f;
import org.joml.Vector3f;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.enums.XInputAxis;

public class XInputXboxControllerAdapter
		extends XInputDeviceAdapter<XboxController> {

	private static final int RUMBLE_MIN = 0;
	private static final int RUMBLE_MAX = 65535;

	/* @formatter: off */
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

	@AdapterMapping
	public static final AnalogMapping<?>
			LT = new XInputAnalogTriggerMapping(XboxController.LT,
					XInputAxis.LEFT_TRIGGER),
			RT = new XInputAnalogTriggerMapping(XboxController.RT,
					XInputAxis.RIGHT_TRIGGER),
			LS = new XInputAnalogStickMapping(XboxController.LS,
					XInputAxis.LEFT_THUMBSTICK_X,
					XInputAxis.LEFT_THUMBSTICK_Y),
			RS = new XInputAnalogStickMapping(XboxController.RS,
					XInputAxis.RIGHT_THUMBSTICK_X,
					XInputAxis.RIGHT_THUMBSTICK_Y);

	@AdapterMapping
	public static final RumbleMapping
		RUMBLE_COARSE = new XInputRumbleMapping(XboxController.RUMBLE_COARSE),
		RUMBLE_FINE = new XInputRumbleMapping(XboxController.RUMBLE_FINE);
	/* @formatter: on */

	private XInputButtons buttons;
	private XInputAxes axes;
	private int rumbleCoarse;
	private int rumbleFine;

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

	@FeatureAdapter
	public void doRumble(XInputRumbleMapping mapping, Vibration1f motor) {
		int force = (int) (RUMBLE_MAX * motor.force);

		/*
		 * The X-input API will throw an exception if it receives a motor force
		 * that is out of its valid bounds. Clamping the force will prevent this
		 * from occurring.
		 */
		if (force < RUMBLE_MIN) {
			force = RUMBLE_MIN;
		} else if (force > RUMBLE_MAX) {
			force = RUMBLE_MAX;
		}

		/*
		 * A comparison is made here to ensure that a vibration force update is
		 * only sent when necessary. It would be horrendous for performance to
		 * send these signals every update call.
		 */
		if (mapping == RUMBLE_COARSE && rumbleCoarse != force) {
			this.rumbleCoarse = force;
			xinput.setVibration(rumbleCoarse, rumbleFine);
		} else if (mapping == RUMBLE_FINE && rumbleFine != force) {
			this.rumbleFine = force;
			xinput.setVibration(rumbleCoarse, rumbleFine);
		}
	}

	@Override
	public void poll() {
		super.poll();

		XInputComponents comps = xinput.getComponents();
		this.axes = comps.getAxes();
		this.buttons = comps.getButtons();
	}

}
