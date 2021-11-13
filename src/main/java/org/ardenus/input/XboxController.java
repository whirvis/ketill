package org.ardenus.input;

import org.ardenus.input.adapter.DeviceAdapter;
import org.ardenus.input.feature.AnalogStick;
import org.ardenus.input.feature.AnalogTrigger;
import org.ardenus.input.feature.DeviceButton;
import org.ardenus.input.feature.FeaturePresent;
import org.ardenus.input.feature.RumbleMotor;

import com.whirvex.event.EventManager;

/**
 * A Microsoft XBOX controller.
 */
@DeviceId("xbox")
public class XboxController extends Controller {

	/* @formatter: off */
	@FeaturePresent
	public static final DeviceButton
			A = new DeviceButton("a"),
			B = new DeviceButton("b"),
			X = new DeviceButton("x"),
			Y = new DeviceButton("y"),
			LB = new DeviceButton("lb"),
			RB = new DeviceButton("rb"),
			GUIDE = new DeviceButton("menu"),
			START = new DeviceButton("pause"),
			THUMB_L = new DeviceButton("ls"),
			THUMB_R = new DeviceButton("rs"),
			UP = new DeviceButton("up", Direction.UP),
			RIGHT = new DeviceButton("right", Direction.RIGHT),
			DOWN = new DeviceButton("down", Direction.DOWN),
			LEFT = new DeviceButton("left", Direction.LEFT);

	@FeaturePresent
	public static final AnalogStick
			LS = new AnalogStick("ls", THUMB_L),
			RS = new AnalogStick("rs", THUMB_R);
	
	@FeaturePresent
	public static final AnalogTrigger
			LT = new AnalogTrigger("lt"),
			RT = new AnalogTrigger("rt");
	
	@FeaturePresent
	public static final RumbleMotor
			RUMBLE_COARSE = new RumbleMotor("coarse_rumble"),
			RUMBLE_FINE = new RumbleMotor("fine_rumble");
	/* @formatter: on */

	/**
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @param adapter
	 *            the XBOX controller adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public XboxController(EventManager events,
			DeviceAdapter<XboxController> adapter) {
		super(events, adapter, LS, RS, LT, RT);
	}

}
