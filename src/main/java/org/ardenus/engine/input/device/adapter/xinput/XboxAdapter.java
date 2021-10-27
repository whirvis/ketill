package org.ardenus.engine.input.device.adapter.xinput;

import java.util.Objects;

import org.ardenus.engine.input.device.XboxController;
import org.ardenus.engine.input.device.adapter.AdapterMapping;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.ardenus.engine.input.device.feature.Button1b;
import org.ardenus.engine.input.device.feature.DeviceButton;
import org.ardenus.engine.input.device.feature.Trigger1f;
import org.ardenus.engine.input.device.feature.Vibration1f;
import org.joml.Vector3f;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.enums.XInputAxis;

/**
 * An adapter which maps input for an XBOX controller via X-input.
 */
public class XboxAdapter extends DeviceAdapter<XboxController> {

	/* @formatter: off */
	@AdapterMapping
	public static final XButtonMapping
			A = new XButtonMapping(XboxController.A, "a"),
			B = new XButtonMapping(XboxController.B, "b"),
			X = new XButtonMapping(XboxController.X, "x"),
			Y = new XButtonMapping(XboxController.Y, "y"),
			LB = new XButtonMapping(XboxController.LB, "lShoulder"),
			RB = new XButtonMapping(XboxController.RB, "rShoulder"),
			GUIDE = new XButtonMapping(XboxController.GUIDE, "guide"),
			START = new XButtonMapping(XboxController.START, "start"),
			THUMB_L = new XButtonMapping(XboxController.THUMB_L, "lThumb"),
			THUMB_R = new XButtonMapping(XboxController.THUMB_R, "rThumb"),
			UP = new XButtonMapping(XboxController.UP, "up"),
			RIGHT = new XButtonMapping(XboxController.RIGHT, "right"),
			DOWN = new XButtonMapping(XboxController.DOWN, "down"),
			LEFT = new XButtonMapping(XboxController.LEFT, "left");

	@AdapterMapping
	public static final XStickMapping
			LS = new XStickMapping(XboxController.LS,
					XInputAxis.LEFT_THUMBSTICK_X,
					XInputAxis.LEFT_THUMBSTICK_Y),
			RS = new XStickMapping(XboxController.RS,
					XInputAxis.RIGHT_THUMBSTICK_X,
					XInputAxis.RIGHT_THUMBSTICK_Y);

	@AdapterMapping
	public static final XTriggerMapping
			LT = new XTriggerMapping(XboxController.LT,
					XInputAxis.LEFT_TRIGGER),
			RT = new XTriggerMapping(XboxController.RT,
					XInputAxis.RIGHT_TRIGGER);

	@AdapterMapping
	public static final XRumbleMapping
			RUMBLE_COARSE = new XRumbleMapping(XboxController.RUMBLE_COARSE),
			RUMBLE_FINE = new XRumbleMapping(XboxController.RUMBLE_FINE);
	/* @formatter: on */

	private static final int RUMBLE_MIN = 0;
	private static final int RUMBLE_MAX = 65535;

	private final XInputDevice xDevice;
	private XInputButtons buttons;
	private XInputAxes axes;
	private int rumbleCoarse;
	private int rumbleFine;

	/**
	 * @param xDevice
	 *            the X-input device.
	 * @throws NullPointerException
	 *             if {@code xinput} is {@code null}.
	 */
	public XboxAdapter(XInputDevice xDevice) {
		this.xDevice = Objects.requireNonNull(xDevice, "xDevice");
	}

	@Override
	public boolean isConnected() {
		return xDevice.isConnected();
	}

	protected boolean isPressed(XStickMapping mapping) {
		DeviceButton zButton = mapping.feature.zButton;
		XButtonMapping zMapping = (XButtonMapping) this.getMapping(zButton);
		return zMapping != null ? zMapping.isPressed(buttons) : false;
	}

	@FeatureAdapter
	public void isPressed(XButtonMapping mapping, Button1b button) {
		button.pressed = mapping.isPressed(buttons);
	}

	@FeatureAdapter
	public void updateStick(XStickMapping mapping, Vector3f stick) {
		stick.x = axes.get(mapping.xAxis);
		stick.y = axes.get(mapping.yAxis);
		stick.z = this.isPressed(mapping) ? -1.0F : 0.0F;
	}

	@FeatureAdapter
	public void updateTrigger(XTriggerMapping mapping, Trigger1f trigger) {
		trigger.force = axes.get(mapping.triggerAxis);
	}

	@FeatureAdapter
	public void doRumble(XRumbleMapping mapping, Vibration1f motor) {
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
			xDevice.setVibration(rumbleCoarse, rumbleFine);
		} else if (mapping == RUMBLE_FINE && rumbleFine != force) {
			this.rumbleFine = force;
			xDevice.setVibration(rumbleCoarse, rumbleFine);
		}
	}

	@Override
	public void poll() {
		xDevice.poll();

		XInputComponents comps = xDevice.getComponents();
		this.axes = comps.getAxes();
		this.buttons = comps.getButtons();
	}

}
